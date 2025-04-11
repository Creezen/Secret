package com.jayce.vexis.member.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.lifecycleScope
import com.creezen.tool.AndroidTool.readPrefs
import com.creezen.tool.AndroidTool.replaceFragment
import com.creezen.tool.AndroidTool.writePrefs
import com.creezen.tool.Constant.BASE_FILE_PATH
import com.creezen.tool.FileTool.getFilePathByUri
import com.creezen.tool.NetTool
import com.creezen.tool.NetTool.await
import com.creezen.tool.NetTool.buildFileMultipart
import com.jayce.vexis.base.BaseActivity
import com.jayce.vexis.R
import com.jayce.vexis.databinding.HomePageBinding
import com.jayce.vexis.member.manage.AdminActivity
import com.jayce.vexis.member.manage.UserBasicInfo
import com.jayce.vexis.member.UserService
import com.jayce.vexis.onlineUser
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomePage : BaseActivity() {
    companion object {
        const val TAG = "HomePage"
    }

    private lateinit var binding: HomePageBinding
    private val userBasicInfo = UserBasicInfo()
    private val userLive = UserLive()
    private var imageLauncher: ActivityResultLauncher<Array<String>>? = null

    override fun registerLauncher() {
        imageLauncher =
            getLauncher(openFile()) {
                if (it == null) return@getLauncher
                val filePath = getFilePathByUri(it) ?: return@getLauncher
                lifecycleScope.launch {
                    val result =
                        NetTool.create<UserService>()
                            .uploadAvatar(
                                onlineUser.userId,
                                buildFileMultipart(filePath, "file")
                            ).await()
                    if (result["status"] == true) {
                        val cursorTime = System.currentTimeMillis()
                        writePrefs {
                            it.putLong("cursorTime", cursorTime)
                        }
                        loadAvatarFromNet(0, cursorTime)
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
            nickname.text = onlineUser.nickname
            id.text = onlineUser.userId
            if (onlineUser.isAdministrator()) {
                administrator.visibility = View.VISIBLE
            }
            replaceFragment(supportFragmentManager, R.id.page, userBasicInfo, false)
            administrator.setOnClickListener {
                startActivity(Intent(this@HomePage, AdminActivity::class.java))
            }
            info.setOnClickListener {
                if (userBasicInfo.isVisible.not()) {
                    replaceFragment(supportFragmentManager, R.id.page, userBasicInfo, false)
                }
            }
            live.setOnClickListener {
                if (userLive.isVisible.not()) {
                    replaceFragment(supportFragmentManager, R.id.page, userLive, false)
                }
            }
            image.setOnClickListener {
                Log.e("HomePage.initPage", "click image")
                imageLauncher?.launch(arrayOf("image/*"))
            }
            val cursorTime =
                readPrefs {
                    it.getLong("cursorTime", 0)
                }
            val cacheKey = "key:$cursorTime"
            NetTool.setImage(
                this@HomePage,
                image,
                "${BASE_FILE_PATH}head/${onlineUser.userId}.png",
                key = AvatarSignnature(cacheKey),
            )
            loadAvatarFromNet(300, cursorTime)
        }
    }

    private fun loadAvatarFromNet(
        delayTime: Long = 0,
        cursorTime: Long? = null,
    ) {
        val key = cursorTime?.let {
            AvatarSignnature("key:$cursorTime")
        }
        lifecycleScope.launch {
            delay(delayTime)
            val old = binding.image.drawable
            NetTool.setImage(
                this@HomePage,
                binding.image,
                "${BASE_FILE_PATH}head/${onlineUser.userId}.png",
                old,
                key = key
            )
        }
    }
}
