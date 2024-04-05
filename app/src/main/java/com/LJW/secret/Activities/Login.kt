package com.ljw.secret.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ljw.secret.Constant.BASE_SOCKET_PATH
import com.ljw.secret.Constant.LOCAL_SOCKET_PORT
import com.ljw.secret.OnlineUser
import com.ljw.secret.R
import com.ljw.secret.UserSocket
import com.ljw.secret.databinding.ActivityLoginBinding
import com.ljw.secret.network.UserService
import com.ljw.secret.util.Map2POJO
import com.ljw.secret.util.NetworkServerCreator
import com.ljw.secret.util.addTextChangedListener
import com.ljw.secret.util.await
import com.ljw.secret.util.msg
import com.ljw.secret.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.Socket

class Login : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView(){
        with(binding){
            name.addTextChangedListener {
                afterTextChanged {
                    it?.apply {
                         login.isClickable = !(this.length>18||it.length<6)
                    }
                }
            }
            findPassword.setOnClickListener {
                "功能还未完善".toast()
            }
            createAccount.setOnClickListener {
                val intent= Intent(this@Login, AccountCreation::class.java)
                intent.putExtra("intentAccount",name.msg())
                startActivity(intent)
            }
            login.setOnClickListener {
                kotlin.runCatching {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val loginResult = NetworkServerCreator.create<UserService>()
                            .LoginSystem(name.msg(), password.msg())
                            .await()
                        if (!loginResult.containsKey("status")) {
                            OnlineUser =loginResult.Map2POJO()
                            UserSocket = async(Dispatchers.IO) {
                                Log.e("Login.initView","socket")
                                Socket(BASE_SOCKET_PATH, LOCAL_SOCKET_PORT)
                            }.await()
                            startActivity(Intent(this@Login, Main::class.java))
                        }else{
                            val status = loginResult["status"]?.toInt()
                            if (status == 0)  "账号不存在, 点击“创建账号”按钮新建一个吧~".toast()
                            else "账号或密码错误，请检查后再次尝试！".toast()
                        }
                    }
                }.onFailure {
                    Log.e("Login.initView","login error: $it")
                }
            }
            name.setText(getString(R.string.login_name))
            password.setText(R.string.login_password)
        }
    }
}