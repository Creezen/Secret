package com.ljw.secret.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.lifecycleScope
import com.ljw.secret.Constant.BASE_FILE_PATH
import com.ljw.secret.OnlineUserItem
import com.ljw.secret.R
import com.ljw.secret.databinding.HomePageBinding
import com.ljw.secret.fragments.UserBasicInfo
import com.ljw.secret.fragments.UserLive
import com.ljw.secret.network.UserService
import com.ljw.secret.util.AndroidUtil
import com.ljw.secret.util.FileUtil
import com.ljw.secret.util.NetUtil
import com.ljw.secret.util.NetworkServerCreator
import com.ljw.secret.util.await
import com.ljw.secret.util.replaceFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomePage: BaseActivity() {

    private lateinit var binding: HomePageBinding
    private val userBasicInfo = UserBasicInfo()
    private val userLive = UserLive()
    private var imageLauncher: ActivityResultLauncher<Array<String>>? = null

    override fun registerLauncher() {
        imageLauncher = getLauncher(openFile()) {
            if (it == null) {
                return@getLauncher
            }
            val filePath = FileUtil.getFilePathByUri(it)
            if (filePath == null) {
                return@getLauncher
            }
            lifecycleScope.launch {
                val result = NetworkServerCreator.create<UserService>()
                    .uploadAvatar(
                        OnlineUserItem.userId,
                        NetUtil.buildFileMultipart(filePath, "file")
                    ).await()
                if(result["status"] == true) {
                    loadAvatarFromNet()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPage()
    }

    private fun initPage() {
        with(binding) {
            nickname.text = OnlineUserItem.nickname
            id.text = OnlineUserItem.userId
            if (OnlineUserItem.administrator == 1) {
                administrator.visibility = View.VISIBLE
            }
            replaceFragment(supportFragmentManager, R.id.page, userBasicInfo , false)
            administrator.setOnClickListener {

            }
            info.setOnClickListener {
                if (userBasicInfo.isVisible.not()) {
                    replaceFragment(supportFragmentManager, R.id.page, userBasicInfo , false)
                }
            }
            live.setOnClickListener {
                if (userLive.isVisible.not()) {
                    replaceFragment(supportFragmentManager, R.id.page, userLive, false)
                }
            }
            image.setOnClickListener {
                imageLauncher?.launch(arrayOf("image/*"))
            }
            val cursorTime = AndroidUtil.readPrefs(this@HomePage) {
                it.getLong("cursorTime", 0)
            }
            val cacheKey = "key:$cursorTime"
            NetUtil.setImage(
                this@HomePage,
                image,
                "${BASE_FILE_PATH}head/${OnlineUserItem.userId}.png",
                cacheKey = cacheKey
            )
            loadAvatarFromNet(300)
        }
    }

    private fun loadAvatarFromNet(delayTime: Long = 0) {
        lifecycleScope.launch {
            delay(delayTime)
            val old = binding.image.drawable
            val cursorTime = System.currentTimeMillis()
            NetUtil.setImage(
                this@HomePage,
                binding.image,
                "${BASE_FILE_PATH}head/${OnlineUserItem.userId}.png",
                old,
                "key:$cursorTime",
            )
            AndroidUtil.writePrefs(this@HomePage){
                it.putLong("cursorTime", cursorTime)
            }
        }
    }
}