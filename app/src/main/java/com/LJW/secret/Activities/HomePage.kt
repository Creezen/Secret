package com.LJW.secret.Activities

import android.os.Bundle
import com.LJW.secret.Activities.BaseActivity
import com.LJW.secret.databinding.HomePageBinding

class HomePage: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding= HomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}