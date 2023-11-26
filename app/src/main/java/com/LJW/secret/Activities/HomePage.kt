package com.ljw.secret.Activities

import android.os.Bundle
import com.ljw.secret.Activities.BaseActivity
import com.ljw.secret.databinding.HomePageBinding

class HomePage: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding= HomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}