package com.LJW.secret.Activities

import android.os.Bundle
import com.LJW.secret.databinding.MtMainBinding

class MultiThread:BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=MtMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}