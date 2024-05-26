package com.ljw.secret.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ljw.secret.Constant.BASE_SOCKET_PATH
import com.ljw.secret.Constant.LOCAL_SOCKET_PORT
import com.ljw.secret.OnlineUserItem
import com.ljw.secret.R
import com.ljw.secret.UserSocket
import com.ljw.secret.databinding.ActivityLoginBinding
import com.ljw.secret.network.UserService
import com.ljw.secret.util.DataUtil.msg
import com.ljw.secret.util.DataUtil.toast
import com.ljw.secret.util.Map2POJO
import com.ljw.secret.util.NetworkServerCreator
import com.ljw.secret.util.addTextChangedListener
import com.ljw.secret.util.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.Socket
import java.util.concurrent.locks.ReentrantLock

class Login : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    companion object {
        var isLogin: Boolean = false
        var loginLock: ReentrantLock = ReentrantLock(true)

        fun setLoginStatus(status: Boolean) {
            loginLock.lock()
            isLogin = status
            loginLock.unlock()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        val packageInfo =
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        "${packageInfo.versionName}  ${packageInfo.longVersionCode}".toast()
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
                val intent = Intent(this@Login, AccountCreation::class.java)
                intent.putExtra("intentAccount",name.msg())
                startActivity(intent)
            }
            login.setOnClickListener {
                kotlin.runCatching {
                    lifecycleScope.launch(Dispatchers.Main) {
                        loginLock.lock()
                        if (isLogin) {
                            return@launch
                        }
                        isLogin = true
                        loginLock.unlock()
                        val loginResult = NetworkServerCreator.create<UserService>()
                            .LoginSystem(name.msg(), password.msg())
                            .await()
                        if (!loginResult.containsKey("status")) {
                            OnlineUserItem = loginResult.Map2POJO()
                            UserSocket = async(Dispatchers.IO) {
                                Socket(BASE_SOCKET_PATH, LOCAL_SOCKET_PORT)
                            }.await()
                            startActivity(Intent(this@Login, Main::class.java))
                        } else {
                            isLogin = false
                            val status = loginResult["status"]?.toInt()
                            if (status == 0) {
                                "账号不存在, 点击“创建账号”按钮新建一个吧~".toast()
                            } else {
                                "账号或密码错误，请检查后再次尝试！".toast()
                            }
                        }
                    }
                }.onFailure {
                    isLogin = false
                    Log.e("Login.initView","login error: $it")
                }
            }
            name.setText(getString(R.string.login_name))
            password.setText(R.string.login_password)
        }
    }
}