package com.jayce.vexis.business.login

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.creezen.commontool.CreezenTool.map2pojo
import com.creezen.commontool.CreezenTool.toTime
import com.creezen.tool.AndroidTool
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.BaseTool.restartApp
import com.creezen.tool.NetTool.await
import com.creezen.tool.NetTool.createApi
import com.creezen.tool.NetTool.setOnlineSocket
import com.creezen.tool.SoundTool.playShortSound
import com.creezen.tool.ThreadTool
import com.creezen.tool.bean.BlockOption
import com.creezen.tool.contract.LifecycleJob
import com.creezen.tool.enum.ThreadType
import com.google.gson.internal.LinkedTreeMap
import com.jayce.vexis.R
import com.jayce.vexis.business.main.MainActivity
import com.jayce.vexis.business.role.register.RegisterActivity
import com.jayce.vexis.core.SessionManager.BASE_FILE_PATH
import com.jayce.vexis.core.SessionManager.BASE_SOCKET_PATH
import com.jayce.vexis.core.SessionManager.LOCAL_SOCKET_PORT
import com.jayce.vexis.core.SessionManager.registerUser
import com.jayce.vexis.databinding.ActivityLoginBinding
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.bean.ApkSimpleEntry
import com.jayce.vexis.foundation.route.ApiService
import com.jayce.vexis.foundation.route.PackageService
import com.jayce.vexis.foundation.route.UserService
import com.jayce.vexis.foundation.view.animator.MyCustomTransformer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.Socket

class LoginActivity : AppCompatActivity() {

    companion object {
        const val TAG = "LoginActivity"
    }

    private lateinit var binding: ActivityLoginBinding
    private val list = arrayListOf<String>()
    private val picAdapter by lazy {
        HomePagePicAdapter(this, list)
    }
    val liveData = MutableLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        binding.root.fitsSystemWindows = true
        setContentView(binding.root)
        getNewestVersion()
        initView()
        setAnimation()
    }

    private fun getNewestVersion() {
        request<PackageService, ApkSimpleEntry>({ getVersion() }) {
            it.apply {
                withContext(Dispatchers.Main) {
                    "${modifyTime.toTime()}  $versionName".toast()
                }
            }
        }
    }

    private fun setAnimation() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.login_button_anim)
        binding.login.startAnimation(animation)
    }

    private fun initView() {
        with(binding) {
            lifecycleOwner = this@LoginActivity
            env = this@LoginActivity
            liveData.observe(this@LoginActivity) {
                login.isClickable = it.length in 6..18
            }
            findPassword.setOnClickListener {
                AndroidTool.writePrefs {
                    it.putString("font", "方正粗圆")
                }
                restartApp()
//              getPinyinForChinese(liveData.value)
            }
            createAccount.setOnClickListener {
                playShortSound(R.raw.delete)
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                intent.putExtra("intentAccount", name.msg())
                startActivity(intent)
            }
            login.setOnClickListener {
                login.isClickable = false
                playShortSound(R.raw.click)
                val option = BlockOption(ThreadType.SINGLE, 2000L, Dispatchers.Main)
                ThreadTool.runWithBlocking(option, object : LifecycleJob {
                    override suspend fun onDispatch() {
                        request<UserService, LinkedTreeMap<String, String>>({ loginSystem(name.msg(), password.msg()) }) {
                            if (!it.containsKey("status")) {
                                registerUser(it.map2pojo())
                                kotlin.runCatching {
                                    val socket = lifecycleScope.async(Dispatchers.IO) {
                                        Socket(BASE_SOCKET_PATH, LOCAL_SOCKET_PORT)
                                    }.await()
                                    setOnlineSocket(socket)
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                }.onFailure {
                                    Log.e(TAG, "socket error: ${it.javaClass.simpleName}  $it")
                                }
                            } else {
                                val status = it["status"]?.toInt()
                                if (status == 0) {
                                    "账号不存在, 点击“创建账号”按钮新建一个吧~".toast()
                                } else {
                                    "账号或密码错误，请检查后再次尝试！".toast()
                                }
                            }
                        }
                    }

                    override fun onTimeoutFinish(isWorkFinished: Boolean) {
                        login.isClickable = true
                    }
                })
            }
            quickStart.setOnClickListener {
                val component = ComponentName("com.DefaultCompany.Myproject", "com.unity3d.player.UnityPlayerActivity")
                val intent = Intent()
                intent.component = component
                startActivity(intent)
            }
            val packageInfo = packageManager.getPackageInfo(
                packageName,
                PackageManager.PackageInfoFlags.of(0),
            )
            val verMsg = "APP : v${packageInfo.versionName}  ${packageInfo.lastUpdateTime.toTime()}"
            versionMsg.text = verMsg
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
            list.add("${BASE_FILE_PATH}bZuTJX1743912177610.jpg")
            list.add("${BASE_FILE_PATH}LsFUqj1743922684602.jpg")
            picAdapter.notifyItemRangeInserted(0, 5)
            setPageTransformer(MyCustomTransformer())
        }
    }

    private fun getPinyinForChinese(chineseChar: String?) {
        if (chineseChar == null) {
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