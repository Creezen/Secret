package com.jayce.vexis.client

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Resources
import android.view.LayoutInflater
import com.jayce.vexis.client.BaseTool.envContext
import com.jayce.vexis.client.ability.api.IActivity
import com.jayce.vexis.client.ability.api.IFragment
import com.jayce.vexis.client.ability.api.ITool
import dalvik.system.DexClassLoader
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.zip.ZipFile

object ModuleHelper {

    private val classMap: ConcurrentHashMap<String, Any> = ConcurrentHashMap()

    fun getFragment(clazzName: String): IFragment? {
        val fragment = classMap[clazzName] as? IFragment
        TLog.d("getFragment: $clazzName  $fragment")
        return fragment
    }

    fun getActivity(clazzName: String): IActivity<Any, Any>? {
        val activity = classMap[clazzName] as? IActivity<Any, Any>
        TLog.d("getActivity: $activity  $clazzName")
        return activity
    }

    fun loadModule(fileName: String, preLoadList: List<String>) {
        val file = FileTool.getDir(FileTool.Dir.LOC_PRIVATE_FILE, envContext) ?: return
        val apkFileList = file.listFiles()?.find {
            it.listFiles()?.get(0)?.name.equals(fileName)
        } ?: return
        val apkFile = apkFileList.listFiles()?.get(0) ?: return
        apkFile.setReadOnly()
        val soPath = extraSOLib(apkFile)
        val loader = DexClassLoader(apkFile.path, null, soPath, envContext.classLoader)
        preLoadList.forEach { loadAAR(envContext, loader, apkFile, it) }
    }

    private fun loadAAR(context: Context, loader: DexClassLoader, file: File, className: String) {
        kotlin.runCatching {
            val clazz = loader.loadClass(className)
            val instance = clazz.getDeclaredConstructor().newInstance()
            val ctx = createPluginContext(context, file.path, loader)
            when (instance) {
                is IFragment -> instance.injectContext(ctx)
                is IActivity<*, *> -> instance.injectContext(ctx)
                is ITool -> {
                    instance.injectContext(ctx)
                    instance.init()
                }
            }
            TLog.d("save class: $className  $instance")
            classMap[className] = instance
        }.onFailure {
            TLog.e("loadAAR error: ${it.message}")
            it.printStackTrace()
        }
    }

    private fun createPluginContext(context: Context, path: String, loader: DexClassLoader): ContextWrapper {
        val assertManager = pluginAssert(context, path)
        val pluginResource = pluginResource(context, assertManager)
        return object : ContextWrapper(context) {
            override fun getResources() = pluginResource
            override fun getAssets() = assertManager
            override fun getClassLoader() = loader
            override fun getApplicationContext() = this

            override fun getTheme(): Resources.Theme {
                val themeId = context.resources.getIdentifier("Theme.Secret", "style", context.packageName)
                val theme = resources.newTheme()
                theme.applyStyle(themeId, true)
                return theme
            }

            override fun getSystemService(name: String): Any? {
                if (name == Context.LAYOUT_INFLATER_SERVICE) {
                    return LayoutInflater.from(context).cloneInContext(this)
                }
                return super.getSystemService(name)
            }
        }
    }

    private fun pluginAssert(context: Context, path: String): AssetManager {
        val assertManager = AssetManager::class.java.newInstance()
        val pathInvoke = assertManager.javaClass.getMethod("addAssetPath", String::class.java)
        pathInvoke.invoke(assertManager, context.packageCodePath)
        pathInvoke.invoke(assertManager, path)
        return assertManager
    }

    private fun pluginResource(context: Context, assetManager: AssetManager): Resources {
        val hostResource = context.resources
        val resource = Resources(
            assetManager,
            hostResource.displayMetrics,
            hostResource.configuration
        )
        return resource
    }

    private fun extraSOLib(pluginApk: File) = ZipFile(pluginApk).use {
        val destFile = File(pluginApk.parent, "so")
        destFile.setWritable(true)
        if (!destFile.exists()) destFile.mkdirs()
        val entries = it.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            if (!entry.name.endsWith(".so")) continue
            val realName = File(entry.name).name
            val soFile = File(destFile, realName)
            if (soFile.exists()) continue
            soFile.parentFile?.mkdirs()
            var length = -1
            val buffer = ByteArray(4096)
            it.getInputStream(entry).use { ins ->
                soFile.outputStream().use { ops ->
                    while (ins.read(buffer).also { length = it } > 0) {
                        ops.write(buffer, 0, length)
                    }
                }
            }
            soFile.setExecutable(true, false)
        }
        destFile.path
    }
}