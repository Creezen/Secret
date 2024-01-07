package com.ljw.secret.activities

import android.os.Bundle
import com.ljw.secret.databinding.HomePageBinding

class HomePage: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding= HomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}