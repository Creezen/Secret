package com.jayce.vexis.business.login

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.ContentValues
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.app.NotificationCompat
import androidx.core.view.drawToBitmap
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.creezen.commontool.bean.ApkSimpleInfo
import com.creezen.commontool.bean.TransferStatusBean
import com.creezen.commontool.bean.UserBean
import com.creezen.commontool.toBean
import com.creezen.commontool.toTime
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.BaseTool
import com.creezen.tool.NetTool
import com.creezen.tool.NetTool.destroySocket
import com.creezen.tool.SoundTool.playShortSound
import com.creezen.tool.ThreadTool
import com.creezen.tool.ThreadTool.ui
import com.creezen.tool.ability.thread.BlockOption
import com.creezen.tool.ability.thread.ThreadType
import com.jayce.vexis.R
import com.jayce.vexis.business.main.MainActivity
import com.jayce.vexis.business.role.register.RegisterActivity
import com.jayce.vexis.core.CoreService
import com.jayce.vexis.core.SessionManager.BASE_FILE_PATH
import com.jayce.vexis.core.SessionManager.registerUser
import com.jayce.vexis.core.SessionManager.user
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityLoginBinding
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.dynamic.ModuleHelper
import com.jayce.vexis.foundation.route.PackageService
import com.jayce.vexis.foundation.route.UserService
import com.jayce.vexis.foundation.view.animator.MyCustomTransformer
import kotlinx.coroutines.Dispatchers

class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    companion object {
        const val TAG = "LoginActivity"
    }

    private val list = arrayListOf<String>()
    private val picAdapter by lazy {
        HomePagePicAdapter(this, list)
    }
    val liveData = MutableLiveData<String>()

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            val binder = service as CoreService.ConnectionBinder
            val notification = buildNotification()
            binder.showNotification(notification)
        }
        override fun onServiceDisconnected(name: ComponentName?) { /**/ }
    }

    private fun buildNotification(): Notification {
        getSystemService(NotificationManager::class.java).apply {
            val notifyChannel = NotificationChannel("1", "login", NotificationManager.IMPORTANCE_HIGH)
            createNotificationChannel(notifyChannel)
        }
        val notification = NotificationCompat.Builder(BaseTool.env(), "1")
            .setSmallIcon(R.drawable.tianji)
            .setContentTitle(getString(R.string.login_success_notify))
            .setContentText(getString(R.string.welcome_user, user().nickname))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setOngoing(true)
            .build()
        return notification
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root.fitsSystemWindows = true
        setContentView(binding.root)
        getNewestVersion()
        initView()
        setAnimation()
        ModuleHelper.test(this, "debug-1.apk")
    }

    override fun onDestroy() {
        super.onDestroy()
        destroySocket()
        unbindService(connection)
    }

    private fun getNewestVersion() {
        request<PackageService, ApkSimpleInfo>({ getVersion() }) {
            ui { "${it.modifyTime.toTime()}  $it.versionName".toast() }
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
                    request<UserService, TransferStatusBean>({ loginSystem(name.msg(), password.msg()) }) {
                        Log.e(TAG, "return message: $it")
                        when(it.statusCode) {
                            -1 -> {
                                it.data.toBean<UserBean>()?.let { user ->
                                    registerUser(user)
                                    NetTool.setUser(user)
                                }
                                bindService(
                                    Intent(this@LoginActivity, CoreService::class.java),
                                    connection,
                                    BIND_AUTO_CREATE
                                )
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
        }
    }

    private fun saveToGallery(view: View) {
        view.post {
            val map = view.drawToBitmap(Bitmap.Config.ARGB_8888)
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "${System.currentTimeMillis()}a.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            val uri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )
            uri?.let {
                kotlin.runCatching {
                    contentResolver.openOutputStream(uri)?.use { stream ->
                        map.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    }
                }.onFailure {
                    Log.e("LJW", "save error")
                }
            }
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
            list.add("${BASE_FILE_PATH}LsFUqj1743922684602.jpg")
            list.add("${BASE_FILE_PATH}bZuTJX1743912177610.jpg")
            list.add("${BASE_FILE_PATH}LsFUqj1743922684602.jpg")
            list.add("${BASE_FILE_PATH}bZuTJX1743912177610.jpg")
            picAdapter.notifyItemRangeInserted(0, 5)
            setPageTransformer(MyCustomTransformer())
            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    if (state == ViewPager2.SCROLL_STATE_IDLE && currentItem == 3) {
                        setCurrentItem(1, false)
                    }
                    if (state == ViewPager2.SCROLL_STATE_IDLE && currentItem == 0) {
                        setCurrentItem(2, false)
                    }
                }
            })
        }
    }
}