package com.jayce.vexis.business.member.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.lifecycleScope
import com.creezen.tool.AndroidTool.readPrefs
import com.creezen.tool.AndroidTool.replaceFragment
import com.creezen.tool.AndroidTool.writePrefs
import com.creezen.tool.FileTool.getFilePathByUri
import com.creezen.tool.NetTool
import com.creezen.tool.NetTool.await
import com.creezen.tool.NetTool.buildFileMultipart
import com.jayce.vexis.core.Config.BASE_FILE_PATH
import com.jayce.vexis.foundation.base.BaseActivity
import com.jayce.vexis.R
import com.jayce.vexis.core.SessionManager.user
import com.jayce.vexis.business.member.manage.AdminActivity
import com.jayce.vexis.business.member.manage.UserBasicInfo
import com.jayce.vexis.business.member.UserService
import com.jayce.vexis.databinding.DashboardBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Dashboard : BaseActivity() {

    companion object {
        const val TAG = "Dashboard"
    }

    private lateinit var binding: DashboardBinding
    private val userBasicInfo = UserBasicInfo()
    private val userLive = UserLive()
    private var imageLauncher: ActivityResultLauncher<Array<String>>? = null

    override fun registerLauncher() {
        imageLauncher = getLauncher(openFile()) {
                if (it == null) return@getLauncher
                val filePath = getFilePathByUri(it) ?: return@getLauncher
                lifecycleScope.launch {
                    val result = NetTool.create<UserService>()
                            .uploadAvatar(
                                user().userId,
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
        binding = DashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPage()
    }

    private fun initPage() {
        with(binding) {
            nickname.text = user().nickname
            id.text = user().userId
            if (user().isAdministrator()) {
                administrator.visibility = View.VISIBLE
            }
            replaceFragment(supportFragmentManager, R.id.page, userBasicInfo,  "userBasicInfo", false)
            administrator.setOnClickListener {
                startActivity(Intent(this@Dashboard, AdminActivity::class.java))
            }
            info.setOnClickListener {
                if (userBasicInfo.isVisible.not()) {
                    replaceFragment(supportFragmentManager, R.id.page, userBasicInfo,  "userBasicInfo",false)
                }
            }
            live.setOnClickListener {
                if (userLive.isVisible.not()) {
                    replaceFragment(supportFragmentManager, R.id.page, userLive, "userLive", false)
                }
            }
            image.setOnClickListener {
                Log.e("Dashboard.initPage", "click image")
                imageLauncher?.launch(arrayOf("image/*"))
            }
            val cursorTime = readPrefs {
                    it.getLong("cursorTime", 0)
                }
            val cacheKey = "key:$cursorTime"
            NetTool.setImage(
                this@Dashboard,
                image,
                "${BASE_FILE_PATH}head/${user().userId}.png",
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
                this@Dashboard,
                binding.image,
                "${BASE_FILE_PATH}head/${user().userId}.png",
                old,
                key = key
            )
        }
    }
}