package com.jayce.vexis.foundation.dynamic

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Resources
import com.creezen.tool.FileTool
import com.creezen.tool.ability.api.IFragment
import dalvik.system.DexClassLoader
import java.io.File

object ModuleHelper {

    private lateinit var ins: IFragment

    fun getInstance(): IFragment {
        return ins
    }

    fun test(context: Context) {
        val file = FileTool.getDir(FileTool.Dir.LOC_PRIVATE_FILE, context)
        var aarFile: File? = null
        file?.listFiles()?.forEach {
            if (it.name.equals("dynamicLib-debug.apk")) {
                aarFile = it
            }
        }
        aarFile?.let {
            it.setReadOnly()
            loadAAR(context, it)
        }
    }

    private fun loadAAR(context: Context, file: File) {
        val loader = DexClassLoader(file.path, null, null, context.classLoader)
        kotlin.runCatching {
            val clazz = loader.loadClass("com.jayce.vexis.dynamic.ToolFragment")
            val instance = clazz.getDeclaredConstructor().newInstance() as IFragment
            val ctx = createPluginContext(context, file.path)
            instance.injectContext(ctx)
            ins = instance
        }.onFailure {
            it.printStackTrace()
        }
    }

    private fun createPluginContext(context: Context, path: String) = object : ContextWrapper(context) {
        override fun getResources(): Resources {
            return pluginResource(path)
        }

        override fun getTheme(): Resources.Theme {
            return resources.newTheme()
        }

        override fun getAssets(): AssetManager {
            return pluginAssert(path)
        }
    }

    private fun pluginAssert(path: String): AssetManager {
        val assertManager = AssetManager::class.java.newInstance()
        val pathInvoke = assertManager.javaClass.getMethod("addAssetPath", String::class.java)
        pathInvoke.invoke(assertManager, path)
        return assertManager
    }

    private fun pluginResource(path: String): Resources {
        val assertManager = pluginAssert(path)
        val resource = Resources(
            assertManager,
            Resources.getSystem().displayMetrics,
            Resources.getSystem().configuration
        )
        return resource
    }
}