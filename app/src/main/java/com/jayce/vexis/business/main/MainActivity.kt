package com.jayce.vexis.business.main

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.fragment.app.Fragment
import com.creezen.tool.AndroidTool.readPrefs
import com.creezen.tool.AndroidTool.replaceFragment
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.NetTool
import com.jayce.vexis.R
import com.jayce.vexis.business.article.ArticleFragment
import com.jayce.vexis.business.chat.ChatActivity
import com.jayce.vexis.business.feedback.Feedback
import com.jayce.vexis.business.kit.KitFragment
import com.jayce.vexis.business.media.MediaLibrarFragment
import com.jayce.vexis.business.member.dashboard.AvatarSignnature
import com.jayce.vexis.business.member.dashboard.DashboardActivity
import com.jayce.vexis.business.peer.PeerFragment
import com.jayce.vexis.business.setting.SettingActivity
import com.jayce.vexis.core.Config.BASE_FILE_PATH
import com.jayce.vexis.core.CoreService
import com.jayce.vexis.core.SessionManager.user
import com.jayce.vexis.databinding.ActivityMainBinding
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.core.base.BaseActivity.ActivityCollector.finishAll
import com.jayce.vexis.core.base.BaseViewModel
import com.jayce.vexis.foundation.view.block.SimpleDialog
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView

class MainActivity : BaseActivity<BaseViewModel>() {

    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var scanLauncher: ActivityResultLauncher<ScanOptions>
    private lateinit var binding: ActivityMainBinding
    private lateinit var avatarView: ImageView
    private lateinit var emailView: ImageView
    private lateinit var chatMsgView: ImageView
    private lateinit var emailBadge: Badge
    private lateinit var chatBadge: Badge
    private var viewHolder: ViewHolder? = null

    inner class ViewHolder(
        val feedback: Feedback,
        val kitFragment: KitFragment,
        val historyFragment: com.jayce.vexis.business.history.HistoryFragment,
        val articleFragment: ArticleFragment,
        val senior: PeerFragment,
        val mediaLibrarFragment: MediaLibrarFragment
    )

    override fun registerLauncher() {
        scanLauncher = getLauncher(ScanContract()){
            val resultContent = it.contents
            if (resultContent.isNullOrBlank() || !resultContent.startsWith("https://com.jayce.vexis")) {
                "无效的二维码链接！！！".toast()
                return@getLauncher
            }
            resultContent.toast()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPage()
        startService(Intent(this, CoreService::class.java))
    }

    override fun onResume() {
        super.onResume()
        chatBadge.badgeNumber = CoreService.getUnreadSize()
        val avatarTimestamp = readPrefs {
                it.getLong("cursorTime", 0)
            }
        NetTool.setImage(
            this@MainActivity,
            avatarView,
            "${BASE_FILE_PATH}head/${user().userId}.png",
            key = AvatarSignnature("key:$avatarTimestamp"),
            isCircle = true,
        )

    }

    private fun initPage() {
        with(binding) {
            if (null == viewHolder) {
                viewHolder = ViewHolder(Feedback(), KitFragment(),
                    com.jayce.vexis.business.history.HistoryFragment(), ArticleFragment(), PeerFragment(), MediaLibrarFragment())
                root.tag = viewHolder
            } else {
                viewHolder = root.tag as ViewHolder
            }
            setSupportActionBar(toolBar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.open_drawer)
            }
            toolBarText.text = navigation.menu.getItem(0).title
            replaceFragment(viewHolder!!.historyFragment, "historicalAxis")
            navigation.setNavigationItemSelectedListener { item ->
                toolBarText.text = item.title
                drawerLayout.closeDrawers()
                viewHolder?.apply {
                    when (item.itemId) {
                        R.id.MainMenuFeedback -> replaceFragment(feedback, "feedback")
                        R.id.MainMenuWidget -> replaceFragment(kitFragment, "kitFragment")
                        R.id.MainMenuTimeline -> replaceFragment(historyFragment, "historicalAxis")
                        R.id.MainMenuSynergy -> replaceFragment(articleFragment, "articleFragment")
                        R.id.MainMenuSenior -> replaceFragment(senior, "senior")
                        R.id.MainMenuResource -> replaceFragment(mediaLibrarFragment, "mediaLibrarFragment")
                    }
                }
                return@setNavigationItemSelectedListener true
            }
            navigation.setCheckedItem(R.id.MainMenuTimeline)
            val headView = navigation.getHeaderView(0) as LinearLayout
            avatarView = headView.findViewById(R.id.avataView)
            emailView = headView.findViewById(R.id.myEmail)
            chatMsgView = headView.findViewById(R.id.myChatMsg)
            avatarView.setOnClickListener {
                startActivity(Intent(this@MainActivity, DashboardActivity::class.java))
            }
            emailBadge = QBadgeView(this@MainActivity).bindTarget(emailView)
            emailBadge.apply {
                setBadgeTextSize(7f, true)
                badgeGravity = Gravity.END or Gravity.TOP
            }
            chatBadge = QBadgeView(this@MainActivity).bindTarget(chatMsgView)
            chatBadge.apply {
                setBadgeTextSize(7f, true)
                badgeGravity = Gravity.END or Gravity.TOP
            }
            chatMsgView.setOnClickListener {
                startActivity(Intent(this@MainActivity, ChatActivity::class.java))
            }
            drawerLayout.addDrawerListener(
                object : DrawerListener {
                    override fun onDrawerSlide(
                        drawerView: View,
                        slideOffset: Float,
                    ) {}

                    override fun onDrawerClosed(drawerView: View) {}

                    override fun onDrawerStateChanged(newState: Int) {}

                    override fun onDrawerOpened(drawerView: View) {
                        chatBadge.badgeNumber = CoreService.getUnreadSize()
                        emailBadge.badgeNumber = 0
                    }
                }
            )
        }
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    SimpleDialog(this@MainActivity).apply {
                        setTitle("消息提醒")
                        setMessage("确定退出吗？")
                        setLeftButton("点错了") { _, dialog ->
                            dialog.dismiss()
                        }
                        setRightButton("退出") { _, _ ->
                            dismiss()
                            finishAll()
                        }
                        show()
                    }
                }
            }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_tool_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                finishAll()
            }
            R.id.scanQRCode -> {
                scanLauncher.launch(ScanOptions().apply {
                    setPrompt("开始扫描二维码")
                    setBeepEnabled(false)
                    setTimeout(5000)
                    setBarcodeImageEnabled(false)
                })
            }
            R.id.setting -> {
                startActivity(Intent(this, SettingActivity::class.java))
            }
            android.R.id.home -> binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
    }

    private fun replaceFragment(fragment: Fragment, fragmentTag: String) {
        replaceFragment(supportFragmentManager, R.id.frame, fragment, fragmentTag)
    }
}