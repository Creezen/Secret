package com.jayce.vexis.foundation.base

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

abstract class BaseActivity <T: BaseViewModel> : AppCompatActivity(), Common {

    protected val model by lazy {
        getViewModel()
    }

    final override fun getViewModel(): T {
        return ViewModelProvider(this)[getViewModelClazz()]
    }

    open fun getViewModelClazz(): Class<T> {
        return BaseViewModel::class.java as Class<T>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        }
    }
}