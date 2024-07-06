package com.ljw.secret.activities

import android.content.Intent
import android.content.pm.ConfigurationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.ljw.secret.Constant.BASE_SOCKET_PATH
import com.ljw.secret.Constant.BASE_URL
import com.ljw.secret.Constant.LOCAL_SOCKET_PORT
import com.ljw.secret.OnlineUserItem
import com.ljw.secret.R
import com.ljw.secret.UserSocket
import com.ljw.secret.databinding.ActivityLoginBinding
import com.ljw.secret.network.ApiService
import com.ljw.secret.network.UserService
import com.ljw.secret.util.DataUtil.msg
import com.ljw.secret.util.Map2POJO
import com.ljw.secret.util.NetworkServerCreator
import com.ljw.secret.util.addTextChangedListener
import com.ljw.secret.util.await
import com.ljw.util.TUtil.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
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
            Glide.with(this@Login).load("$BASE_URL/FileSystem/njduhB1716695687532.jpg").into(iv1)
            Glide.with(this@Login).load("$BASE_URL/FileSystem/EHVubX1716696947911.jpg").into(iv2)
            name.addTextChangedListener {
                afterTextChanged {
                    it?.apply {
                         login.isClickable = !(this.length>18||it.length<6)
                    }
                }
            }
            findPassword.setOnClickListener {
                lifecycleScope.launch {
                    val res = NetworkServerCreator.createApi<ApiService>()
                        .getDictionary(name.msg())
                        .await()
                    val jsonObj = JSONObject(res)
                    val data = jsonObj.optJSONArray("data")?.get(0) as JSONObject
                    val realVal = data.optString("pinyin")
                    launch(Dispatchers.Main) {
                        realVal.toast()
                    }
                }

//                "功能还未完善".toast()
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