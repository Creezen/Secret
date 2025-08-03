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
import com.google.gson.internal.LinkedTreeMap
import com.jayce.vexis.core.Config.BASE_FILE_PATH
import com.jayce.vexis.foundation.base.BaseActivity
import com.jayce.vexis.R
import com.jayce.vexis.core.SessionManager.user
import com.jayce.vexis.business.member.manage.AdminActivity
import com.jayce.vexis.business.member.manage.UserBasicInfoFragment
import com.jayce.vexis.foundation.route.UserService
import com.jayce.vexis.databinding.DashboardBinding
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DashboardActivity : BaseActivity<BaseViewModel>() {

    companion object {
        const val TAG = "DashboardActivity"
    }

    private lateinit var binding: DashboardBinding
    private val userBasicInfoFragment = UserBasicInfoFragment()
    private val userLiveFragment = UserLiveFragment()
    private var imageLauncher: ActivityResultLauncher<Array<String>>? = null

    override fun registerLauncher() {
        imageLauncher = getLauncher(openFile()) {
            if (it == null) return@getLauncher
            val filePath = getFilePathByUri(it) ?: return@getLauncher
            request<UserService, LinkedTreeMap<String, Boolean>>({
                uploadAvatar(user().userId, buildFileMultipart(filePath, "file"))
            }) {
                if (it["status"] == true) {
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
            replaceFragment(supportFragmentManager, R.id.page, userBasicInfoFragment,  "userBasicInfoFragment", false)
            administrator.setOnClickListener {
                startActivity(Intent(this@DashboardActivity, AdminActivity::class.java))
            }
            info.setOnClickListener {
                if (userBasicInfoFragment.isVisible.not()) {
                    replaceFragment(supportFragmentManager, R.id.page, userBasicInfoFragment,  "userBasicInfoFragment",false)
                }
            }
            live.setOnClickListener {
                if (userLiveFragment.isVisible.not()) {
                    replaceFragment(supportFragmentManager, R.id.page, userLiveFragment, "userLiveFragment", false)
                }
            }
            image.setOnClickListener {
                Log.e("DashboardActivity.initPage", "click image")
                imageLauncher?.launch(arrayOf("image/*"))
            }
            val cursorTime = readPrefs {
                    it.getLong("cursorTime", 0)
                }
            val cacheKey = "key:$cursorTime"
            NetTool.setImage(
                this@DashboardActivity,
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
                this@DashboardActivity,
                binding.image,
                "${BASE_FILE_PATH}head/${user().userId}.png",
                old,
                key = key
            )
        }
    }
}