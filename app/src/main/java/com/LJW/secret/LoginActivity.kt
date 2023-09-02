package com.LJW.secret

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.LJW.secret.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityLoginBinding.inflate(layoutInflater)
        with(binding){
            loginCountName.setText("123456")
            loginCountPassward.setText("123456")
            findPassword.setOnClickListener {
                Toast.makeText(this@LoginActivity,"暂不支持此功能...",Toast.LENGTH_LONG).show()
            }
        }
        setContentView(binding.root)
    }
}