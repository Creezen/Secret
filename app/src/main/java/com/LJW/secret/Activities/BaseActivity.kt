package com.ljw.secret.activities

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ljw.secret.Common

open class BaseActivity : AppCompatActivity(), Common {

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