package com.ljw.secret.Activities.SubModule

import android.os.Bundle
import com.ljw.secret.Activities.BaseActivity
import com.ljw.secret.databinding.MtMainBinding

class MultiThread: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=MtMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}