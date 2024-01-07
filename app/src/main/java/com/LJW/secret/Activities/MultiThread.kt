package com.ljw.secret.activities

import android.os.Bundle
import com.ljw.secret.activities.BaseActivity
import com.ljw.secret.databinding.MtMainBinding

class MultiThread: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=MtMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}