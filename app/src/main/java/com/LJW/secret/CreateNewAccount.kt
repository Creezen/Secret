package com.LJW.secret

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.LJW.secret.databinding.CreateNewAccountBinding

class CreateNewAccount : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=CreateNewAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}