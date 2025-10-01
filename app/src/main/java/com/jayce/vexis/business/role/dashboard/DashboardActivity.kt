package com.jayce.vexis.business.role.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.creezen.tool.AndroidTool.readPrefs
import com.creezen.tool.AndroidTool.writePrefs
import com.creezen.tool.FileTool.getFilePathByUri
import com.creezen.tool.NetTool
import com.creezen.tool.NetTool.buildFileMultipart
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.internal.LinkedTreeMap
import com.jayce.vexis.business.role.manage.AdminActivity
import com.jayce.vexis.core.SessionManager.BASE_FILE_PATH
import com.jayce.vexis.core.SessionManager.user
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.DashboardBinding
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.route.UserService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DashboardActivity : BaseActivity<DashboardBinding>() {

    companion object {
        const val TAG = "DashboardActivity"
    }

    private val userBasicInfoFragment = UserBasicInfoFragment()
    private val userLiveFragment = UserLiveFragment()
    private val fragmentList = arrayListOf<Fragment>()
    private val adapter = DashboardAdapter(supportFragmentManager, lifecycle, fragmentList)
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
        prepareFragment()
        initPage()
    }

    private fun prepareFragment() {
        fragmentList.add(userBasicInfoFragment)
        fragmentList.add(userLiveFragment)
    }

    private fun initPage() {
        with(binding) {
            nickname.text = user().nickname
            id.text = user().userId
            if (user().getAdministratorStatus()) {
                administrator.visibility = View.VISIBLE
            }
            administrator.setOnClickListener {
                startActivity(Intent(this@DashboardActivity, AdminActivity::class.java))
            }
            page.adapter = adapter
            TabLayoutMediator(tab, page) { tab, pos ->
                val textView = TextView(this@DashboardActivity)
                textView.gravity = Gravity.CENTER
                textView.text = when (pos) {
                    0 -> "基本信息"
                    1 -> "创作动态"
                    else -> ""
                }
                tab.customView = textView
            }.attach()
            image.setOnClickListener {
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