package com.jayce.vexis.login

import android.animation.AnimatorInflater
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.creezen.tool.AndroidTool
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.BaseTool.restartApp
import com.creezen.tool.Constant.BASE_FILE_PATH
import com.creezen.tool.Constant.BASE_SOCKET_PATH
import com.creezen.tool.Constant.LOCAL_SOCKET_PORT
import com.creezen.tool.DataTool.map2pojo
import com.creezen.tool.NetTool
import com.creezen.tool.NetTool.await
import com.creezen.tool.NetTool.createApi
import com.creezen.tool.SoundTool.playShortSound
import com.jayce.vexis.Main
import com.jayce.vexis.R
import com.jayce.vexis.databinding.ActivityLoginBinding
import com.jayce.vexis.member.UserService
import com.jayce.vexis.member.register.AccountCreation
import com.jayce.vexis.onlineSocket
import com.jayce.vexis.onlineUser
import com.jayce.vexis.stylized.animator.MyCustomTransformer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.Socket
import java.util.concurrent.locks.ReentrantLock

class Login : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private val list = arrayListOf<String>()
    private val picAdapter by lazy {
        HomePagePicAdapter(this, list)
    }
    val liveData = MutableLiveData<String>()

    companion object {
        var isLogin: Boolean = false
        var loginLock: ReentrantLock = ReentrantLock(true)

        fun setLoginStatus(status: Boolean) {
            loginLock.lock()
            isLogin = status
            loginLock.unlock()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        setAnimation()
        val packageInfo =
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        "${packageInfo.versionName}  ${packageInfo.longVersionCode}".toast()
//        sendBroadcast(Intent(BROADCAST_LOGOUT).apply {
//            `package` = packageName
//        })
    }

    private fun setAnimation() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.login_button_anim)
        binding.login.startAnimation(animation)
        val animatorSet = AnimatorInflater.loadAnimator(this, R.animator.login_canvas_animator)
        animatorSet.setTarget(binding.iv2)
        animatorSet.start()
    }

    private fun initView(){
        with(binding){
            lifecycleOwner = this@Login
            env = this@Login
            liveData.observe(this@Login) {
                login.isClickable = it.length in 6..18
            }
            findPassword.setOnClickListener {
                AndroidTool.writePrefs {
                    it.putString("font", "方正粗圆")
                }
                restartApp()
                getPinyinForChinese(liveData.value)
            }
            createAccount.setOnClickListener {
                playShortSound(R.raw.delete)
                val intent = Intent(this@Login, AccountCreation::class.java)
                intent.putExtra("intentAccount",name.msg())
                startActivity(intent)
            }
            login.setOnClickListener {
                playShortSound(R.raw.click)
                kotlin.runCatching {
                    lifecycleScope.launch(Dispatchers.Main) {
                        loginLock.lock()
                        if (isLogin) {
                            return@launch
                        }
                        isLogin = true
                        loginLock.unlock()
                        val loginResult = NetTool.create<UserService>()
                            .loginSystem(name.msg(), password.msg())
                            .await()
                        if (!loginResult.containsKey("status")) {
                            onlineUser = loginResult.map2pojo()
                            onlineSocket = async(Dispatchers.IO) {
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
            liveData.value = getString(R.string.login_name)
            password.setText(R.string.login_password)

            initPicture()
        }
    }

    private fun initPicture() {
        binding.iv1.apply {
            this.adapter = picAdapter
            offscreenPageLimit = 3
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            val childAt = getChildAt(0)
            childAt.setPadding(0, 0, 0, 0)
            clipToPadding = false
            list.add("${BASE_FILE_PATH}bEOVqK1713667655864.jpg")
            list.add("${BASE_FILE_PATH}cBfyRz1716695543925.jpg")
            list.add("${BASE_FILE_PATH}GfRPrs1716640205738.jpg")
            list.add("${BASE_FILE_PATH}MHcneb1716619071218.jpg")
            list.add("${BASE_FILE_PATH}njduhB1716695687532.jpg")
            picAdapter.notifyItemRangeInserted(0,5)
            setPageTransformer(MyCustomTransformer())
        }
    }

    private fun getPinyinForChinese(chineseChar: String?) {
        if(chineseChar == null) {
            return
        }
        lifecycleScope.launch {
            kotlin.runCatching {
                val res = createApi<ApiService>()
                    .getDictionary(chineseChar)
                    .await()
                val jsonObj = JSONObject(res)
                val data = jsonObj.optJSONArray("data")?.get(0) as JSONObject
                val realVal = data.optString("pinyin")
                launch(Dispatchers.Main) {
                    realVal.toast()
                }
            }.onFailure {
                "错误，请将账号换成单个汉字再试".toast()
            }
        }
    }
}