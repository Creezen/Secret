package com.ljw.secret.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ljw.secret.Common
import com.ljw.secret.util.ActivityCollector

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
}