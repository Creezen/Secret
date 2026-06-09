package com.jayce.vexis.business.role.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.creezen.commontool.Config.AVATAR_SAVE_TIME
import com.creezen.commontool.Config.MEDIA_TYPE_IMAGE
import com.creezen.commontool.Config.NIL
import com.creezen.tool.AndroidTool.getData
import com.creezen.tool.AndroidTool.putData
import com.creezen.tool.FileTool.getFilePathByUri
import com.creezen.tool.NetTool.buildFileMultipart
import com.creezen.tool.ThreadTool.runOnIO
import com.creezen.tool.ThreadTool.ui
import com.google.android.material.tabs.TabLayoutMediator
import com.jayce.vexis.R
import com.jayce.vexis.StatusManager.liveUser
import com.jayce.vexis.business.role.manage.AdminActivity
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.DashboardBinding
import com.jayce.vexis.domain.route.UserService
import com.jayce.vexis.foundation.Util.Extension.load
import com.jayce.vexis.foundation.Util.request
import kotlinx.coroutines.delay

class DashboardActivity : BaseActivity<DashboardBinding>() {

    private val userBasicInfoFragment = UserBasicInfoFragment()
    private val userLiveFragment = UserLiveFragment()
    private val fragmentList = arrayListOf<Fragment>()
    private val adapter = DashboardAdapter(supportFragmentManager, lifecycle, fragmentList)
    private var imageLauncher: ActivityResultLauncher<Array<String>>? = null

    override fun registerLauncher() {
        imageLauncher = getLauncher(openFile()) {
            if (it == null) return@getLauncher
            val filePath = getFilePathByUri(it) ?: return@getLauncher
            val id = liveUser.userId
            val part = buildFileMultipart(filePath, "file")
            request<UserService, Boolean>({ uploadAvatar(id, part) }) { result ->
                if (result) {
                    val cursorTime = System.currentTimeMillis()
                    putData(AVATAR_SAVE_TIME, cursorTime)
                    loadAvatarFromNet(0, cursorTime)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareFragment()
        initPage()
    }

    private fun prepareFragment() {
        fragmentList.add(userBasicInfoFragment)
        fragmentList.add(userLiveFragment)
    }

    private fun initPage() = binding.apply {
        nickname.userName = liveUser.nickname
        id.text = liveUser.userId
        if (liveUser.isAdministrator()) administrator.visibility = View.VISIBLE
        administrator.setOnClickListener {
            startActivity(Intent(this@DashboardActivity, AdminActivity::class.java))
        }
        page.adapter = adapter
        TabLayoutMediator(tab, page) { tab, pos ->
            val textView = TextView(this@DashboardActivity)
            textView.gravity = Gravity.CENTER
            textView.text = when (pos) {
                0 -> getString(R.string.base_info)
                1 -> getString(R.string.creation_live)
                else -> NIL
            }
            tab.customView = textView
        }.attach()
        image.setOnClickListener { imageLauncher?.launch(arrayOf(MEDIA_TYPE_IMAGE)) }
        runOnIO {
            val time = getData(AVATAR_SAVE_TIME, 0L)
            val url = "${liveUser.userId}.png"
            ui {
                image.load(url, placeHolder = null, it.toString(), true)
                loadAvatarFromNet(300, time)
            }
        }
    }

    private suspend fun loadAvatarFromNet(delayTime: Long = 0, cursorTime: Long? = null) {
        delay(delayTime)
        val old = binding.image.drawable
        val url = "${liveUser.userId}.png"
        val key = cursorTime.toString()
        binding.image.load(url, old, key, true)
    }
}