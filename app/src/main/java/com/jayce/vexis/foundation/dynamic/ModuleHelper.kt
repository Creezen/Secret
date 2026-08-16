package com.jayce.vexis.foundation.dynamic

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Resources
import com.jayce.vexis.client.FileTool
import com.jayce.vexis.client.TLog
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
        return classMap[clazzName] as? IFragment
    }

    fun getActivity(clazzName: String): IActivity? {
        return classMap[clazzName] as? IActivity
    }

    fun loadModule(context: Context, fileName: String, preLoadList: List<String>) {
        val file = FileTool.getDir(FileTool.Dir.LOC_PRIVATE_FILE, context) ?: return
        val apkFile = file.listFiles()?.find { it.name.equals(fileName) } ?: return
        apkFile.setReadOnly()
        val soPath = extraSOLib(apkFile)
        val loader = DexClassLoader(apkFile.path, null, soPath, context.classLoader)
        preLoadList.forEach { loadAAR(context, loader, apkFile, it) }
    }

    private fun loadAAR(context: Context, loader: DexClassLoader, file: File, className: String) {
        kotlin.runCatching {
            val clazz = loader.loadClass(className)
            val instance = clazz.getDeclaredConstructor().newInstance()
            val ctx = createPluginContext(context, file.path, loader)
            when (instance) {
                is IFragment -> instance.injectContext(ctx)
                is IActivity -> instance.injectContext(ctx)
                is ITool -> {
                    instance.injectContext(ctx)
                    instance.init()
                }
            }
            classMap[className] = instance
        }.onFailure {
            TLog.e("loadAAR error: ${it.message}")
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
            soFile.setExecutable(true)
        }
        destFile.path
    }
}