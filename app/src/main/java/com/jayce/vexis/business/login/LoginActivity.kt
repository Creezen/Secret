package com.jayce.vexis.business.login

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.addCallback
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.creezen.commontool.bean.ApkSimpleInfo
import com.creezen.commontool.bean.TransferStatusBean
import com.creezen.commontool.toTime
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.SoundTool.playShortSound
import com.creezen.tool.ThreadTool
import com.creezen.tool.ThreadTool.ui
import com.creezen.tool.ability.thread.BlockOption
import com.creezen.tool.ability.thread.ThreadType
import com.jayce.vexis.R
import com.jayce.vexis.StatusManager.BASE_FILE_PATH
import com.jayce.vexis.business.role.register.RegisterActivity
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityLoginBinding
import com.jayce.vexis.domain.route.PackageService
import com.jayce.vexis.domain.route.UserService
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.ability.Logger
import com.jayce.vexis.foundation.ui.animator.MyCustomTransformer
import kotlinx.coroutines.Dispatchers

class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    companion object {
        const val TAG = "LoginActivity"
    }

    private val list = arrayListOf<String>()
    private val picAdapter by lazy { HomePagePicAdapter(this, list) }
    val liveData = MutableLiveData<String>()

    @Logger(a = "hello")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root.fitsSystemWindows = true
        setContentView(binding.root)
        getNewestVersion()
        initView()
        setAnimation()
    }

    private fun getNewestVersion() {
        request<PackageService, ApkSimpleInfo>({ getVersion() }) {
            ui { "${it.modifyTime.toTime()}  ${it.versionName}".toast() }
        }
    }

    private fun setAnimation() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.login_button_anim)
        binding.login.startAnimation(animation)
    }

    private fun initView() = binding.apply {
        lifecycleOwner = this@LoginActivity
        env = this@LoginActivity
        liveData.observe(this@LoginActivity) {
            login.isClickable = it.length in 6..18
        }
        findPassword.setOnClickListener {
            getString(R.string.not_support).toast()
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
            ThreadTool.runWithBlocking(option) {
                request<UserService, TransferStatusBean>({ loginSystem(name.msg(), password.msg()) }) { response ->
                    when (response.statusCode) {
                        -1 -> {
                            val logIntent = intent.also {
                                it.putExtra("launchResult", true)
                                it.putExtra("launchValue", response.data)
                            }
                            setResult(RESULT_OK, logIntent)
                            finish()
                        }
                        0 -> getString(R.string.no_account_and_create).toast()
                        -3 -> getString(R.string.account_login_other_device).toast()
                        else -> getString(R.string.password_error).toast()
                    }
                }
            }.onTimedOut {
                login.isClickable = true
            }.onComplete {
                login.isClickable = true
            }
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

        onBackPressedDispatcher.addCallback {
            val logIntent = intent.also {
                it.putExtra("launchResult", false)
            }
            setResult(RESULT_OK, logIntent)
            finish()
        }
    }

    private fun initPicture() {
        binding.iv1.apply {
            this.adapter = picAdapter
            offscreenPageLimit = 2
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            val childAt = getChildAt(0)
            childAt.setPadding(0, 0, 0, 0)
            clipToPadding = false
            list.add("${BASE_FILE_PATH}LsFUqj1743922684602.jpg")
            list.add("${BASE_FILE_PATH}bZuTJX1743912177610.jpg")
            list.add("${BASE_FILE_PATH}hXklnq1757767353397.jpg")
            list.add("${BASE_FILE_PATH}GsMmcS1748872115532.jpg")
            list.add("${BASE_FILE_PATH}LsFUqj1743922684602.jpg")
            list.add("${BASE_FILE_PATH}bZuTJX1743912177610.jpg")
            picAdapter.notifyItemRangeInserted(0, list.size)
            setCurrentItem(1, false)
            setPageTransformer(MyCustomTransformer())
            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    if (state != ViewPager2.SCROLL_STATE_IDLE) return
                    if (currentItem == list.size - 1) {
                        post {
                            setCurrentItem(1, false)
                        }
                    }
                    if (currentItem == 0) {
                        post {
                            setCurrentItem(list.size - 2, false)
                        }
                    }
                }
            })
        }
    }
}