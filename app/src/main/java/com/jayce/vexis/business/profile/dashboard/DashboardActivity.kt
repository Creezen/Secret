package com.jayce.vexis.business.profile.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.jayce.vexis.util.Config.AVATAR_SAVE_TIME
import com.jayce.vexis.util.Config.MEDIA_TYPE_IMAGE
import com.jayce.vexis.util.Config.NIL
import com.jayce.vexis.client.AndroidTool.getData
import com.jayce.vexis.client.AndroidTool.putData
import com.jayce.vexis.client.FileTool.getFilePathByUri
import com.jayce.vexis.client.NetTool.buildFileMultipart
import com.jayce.vexis.client.ThreadTool.runOnIO
import com.jayce.vexis.client.ThreadTool.ui
import com.google.android.material.tabs.TabLayoutMediator
import com.jayce.vexis.R
import com.jayce.vexis.StatusManager.liveUser
import com.jayce.vexis.business.profile.dashboard.fragment.UserBasicInfoFragment
import com.jayce.vexis.business.profile.dashboard.fragment.UserLiveFragment
import com.jayce.vexis.business.profile.manage.AdminActivity
import com.jayce.vexis.client.AndroidTool.toast
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.DashboardBinding
import com.jayce.vexis.domain.route.UserService
import com.jayce.vexis.foundation.Util.Extension.jumpTo
import com.jayce.vexis.foundation.Util.Extension.load
import com.jayce.vexis.foundation.Util.Extension.onFalse
import com.jayce.vexis.foundation.Util.Extension.onTrue
import com.jayce.vexis.foundation.Util.request

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
                if (!result) return@request
                val cursorTime = System.currentTimeMillis()
                putData(AVATAR_SAVE_TIME, cursorTime)

                val url = "${liveUser.userId}.png"
                val placeHolder = binding.image.drawable
                val key = cursorTime.toString()
                binding.image.load(url, placeHolder, key, true)
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
        if (liveUser.isAdministrator()) manager.visibility = View.VISIBLE
        manager.setOnClickListener { jumpTo(AdminActivity::class.java) }
        follow.setOnClickListener {
            request<UserService, Int>({ followUser(liveUser.userId, liveUser.userId) }) {
                when (it) {
                    -1 -> { "你已经关注了该用户".toast() }
                    0 -> { "关注失败".toast() }
                    1 -> {
                        follow.visibility = View.GONE
                        "关注成功".toast()
                    }
                }
            }
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
            val time = getData(AVATAR_SAVE_TIME, 0L).toString()
            val url = "${liveUser.userId}.png"
            ui { image.load(url, placeHolder = null, time, true, true) }
        }
    }
}