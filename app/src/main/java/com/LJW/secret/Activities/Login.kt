package com.ljw.secret.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ljw.secret.BASE_SOCKET_PATH
import com.ljw.secret.LOCAL_SOCKET_PORT
import com.ljw.secret.Map2POJO
import com.ljw.secret.Network.UserService
import com.ljw.secret.NetworkServerCreator
import com.ljw.secret.OnlineUser
import com.ljw.secret.UserSocket
import com.ljw.secret.addTextChangedListener
import com.ljw.secret.await
import com.ljw.secret.databinding.ActivityLoginBinding
import com.ljw.secret.msg
import com.ljw.secret.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.ProtocolException
import java.net.Socket

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
                "功能还未完善".toast()
            }
            createAccount.setOnClickListener {
                val intent= Intent(this@Login, AccountCreation::class.java)
                intent.putExtra("intentAccount",name.msg())
                startActivity(intent)
            }
            login.setOnClickListener {
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        var loginResult = NetworkServerCreator.create<UserService>()
                            .LoginSystem(name.msg(), password.msg())
                            .await()
                        if (!loginResult.containsKey("status")) {
                            OnlineUser =loginResult.Map2POJO()
                            UserSocket = async(Dispatchers.IO) {
                                 Socket(BASE_SOCKET_PATH, LOCAL_SOCKET_PORT)
                            }.await()
                            startActivity(Intent(this@Login, Main::class.java))
                        }else{
                            val status=loginResult["status"]?.toInt()
                            if (status == 0)  "账号不存在, 点击“创建账号”按钮新建一个吧~".toast()
                            else "账号或密码错误，请检查后再次尝试！".toast()
                        }
                    }catch (e:RuntimeException){
                        "网络错误，请检查网络后重试！！！".toast()
                    }catch (e1:ProtocolException){
                        "网络错误，请检查网络后重试！！！".toast()
                    }
                }
            }
            name.setText("123456")
            password.setText("123456")
        }
    }
}