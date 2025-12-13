package com.jayce.vexis.foundation.dynamic

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import com.creezen.tool.FileTool
import com.creezen.tool.ability.api.IActivity
import com.creezen.tool.ability.api.IFragment
import dalvik.system.DexClassLoader
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object ModuleHelper {

    private var loader: DexClassLoader? = null
    private val classMap: ConcurrentHashMap<String, Any> = ConcurrentHashMap()

    fun getFragment(clazzName: String): IFragment? {
        return classMap[clazzName] as? IFragment
    }

    fun getActivity(clazzName: String): IActivity? {
        return classMap[clazzName] as? IActivity
    }

    fun test(context: Context, fileName: String) {
        val file = FileTool.getDir(FileTool.Dir.LOC_PRIVATE_FILE, context)
        var aarFile: File? = null
        file?.listFiles()?.forEach {
            if (it.name.equals(fileName)) {
                aarFile = it
            }
        }
        val finalFile = aarFile ?: return
        finalFile.setReadOnly()
        loader = DexClassLoader(finalFile.path, null, null, context.classLoader)
        loader?.apply {
            val list = listOf(
                "com.jayce.vexis.dynamic.ToolFragment",
                "com.jayce.vexis.dynamic.JumpActivity"
            )
            list.forEach { clazzName ->
                loadAAR(context, this, finalFile, clazzName)
            }
        }
    }

    private fun loadAAR(
        context: Context,
        loader: DexClassLoader,
        file: File,
        className: String
    ) {
        kotlin.runCatching {
            val clazz = loader.loadClass(className)
            val instance = clazz.getDeclaredConstructor().newInstance()
            val ctx = createPluginContext(context, file.path)
            when (instance) {
                is IFragment -> { instance.injectContext(ctx) }
                is IActivity -> { instance.injectContext(ctx) }
            }
            classMap[className] = instance
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