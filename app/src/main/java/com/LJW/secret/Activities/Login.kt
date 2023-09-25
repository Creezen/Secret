package com.LJW.secret.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.LJW.secret.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding){
            loginCountName.setText("123456")
            loginCountPassward.setText("123456")
            findPassword.setOnClickListener {
                Toast.makeText(this@Login,"暂不支持此功能...",Toast.LENGTH_LONG).show()
            }
            createAccount.setOnClickListener {
                val intent= Intent(this@Login, AccountCreation::class.java)
                startActivity(intent)
            }
            loginButton.setOnClickListener {
                val intent= Intent(this@Login, Main::class.java)
                startActivity(intent)
            }
        }
    }
}