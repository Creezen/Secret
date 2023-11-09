package com.LJW.secret.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.LJW.secret.Map2POJO
import com.LJW.secret.Network.UserService
import com.LJW.secret.NetworkServerCreator
import com.LJW.secret.OnlineUser
import com.LJW.secret.POJO.User
import com.LJW.secret.addTextChangedListener
import com.LJW.secret.await
import com.LJW.secret.databinding.ActivityLoginBinding
import com.LJW.secret.msg
import com.LJW.secret.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Login : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    fun initView(){
        with(binding){
            name.addTextChangedListener {
                afterTextChanged {
                    it?.apply {
                         login.isClickable = if (this.length>18||it.length<6) false else true
                    }
                }
            }
            findPassword.setOnClickListener {
                Toast.makeText(this@Login,"功能还未完善", Toast.LENGTH_LONG).show()
            }
            createAccount.setOnClickListener {
                val intent= Intent(this@Login, com.LJW.secret.Activities.AccountCreation::class.java)
                intent.putExtra("intentAccount",name.msg())
                startActivity(intent)
            }
            login.setOnClickListener {
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        var loginResult = NetworkServerCreator.create<UserService>()
                            .LoginSystem(name.msg(), password.msg())
                            .await()
                        Log.e("Login.initView", "${loginResult}")
                        if (!loginResult.containsKey("status")) {
                            OnlineUser =loginResult.Map2POJO()
                            startActivity(
                                Intent(this@Login, Main::class.java)
                            )
                        }else{
                            val status=loginResult["status"]?.toInt()
                            if (status == 0)  "账号不存在, 点击“创建账号”按钮新建一个吧~".toast(this@Login)
                            else "账号或密码错误，请检查后再次尝试！".toast(this@Login)
                        }
                    }catch (e:RuntimeException){
                        "网络错误，请检查网络后重试！！！".toast(this@Login)
                    }

                }
            }
            name.setText("123456")
            password.setText("123456")
        }
    }
}