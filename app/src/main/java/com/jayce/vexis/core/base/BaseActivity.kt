package com.jayce.vexis.core.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.creezen.tool.NetTool.destroySocket
import java.util.ArrayList

abstract class  BaseActivity<K: ViewBinding> : AppCompatActivity(), Common<K> {

    val binding: K by lazy {
        getBind()
    }

    final override fun getLayoutInflate(): LayoutInflater {
        return layoutInflater
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root.fitsSystemWindows = true
        setContentView(binding.root)
        ActivityCollector.addActivity(this)
        registerLauncher()
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }

    open fun registerLauncher() {}

    object ActivityCollector {
        private val activities = ArrayList<Activity>()

        fun addActivity(activity: Activity) {
            activities.add(activity)
        }

        fun removeActivity(activity: Activity) {
            activities.remove(activity)
        }

        fun finishAll() {
            activities.forEach {
                if (!it.isFinishing) {
                    it.finish()
                }
            }
            destroySocket()
        }
    }
}