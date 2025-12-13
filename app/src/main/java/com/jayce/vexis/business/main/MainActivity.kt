package com.jayce.vexis.business.main

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.fragment.app.Fragment
import com.creezen.commontool.Config.FragmentTag.FRAGMENT_ARTICLE
import com.creezen.commontool.Config.FragmentTag.FRAGMENT_FEEDBACK
import com.creezen.commontool.Config.FragmentTag.FRAGMENT_FILE
import com.creezen.commontool.Config.FragmentTag.FRAGMENT_HISTORY
import com.creezen.commontool.Config.FragmentTag.FRAGMENT_KIT
import com.creezen.commontool.Config.FragmentTag.FRAGMENT_MAP
import com.creezen.commontool.Config.FragmentTag.FRAGMENT_SENIOR
import com.creezen.commontool.Config.PreferenceParam.AVATAR_SAVE_TIME
import com.creezen.commontool.Config.QRCodeParam.URL_PREFIX
import com.creezen.tool.AndroidTool.readPrefs
import com.creezen.tool.AndroidTool.replaceFragment
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.NetTool
import com.jayce.vexis.R
import com.jayce.vexis.business.article.ArticleFragment
import com.jayce.vexis.business.chat.ChatActivity
import com.jayce.vexis.business.feedback.FeedbackFragment
import com.jayce.vexis.business.file.FileFragment
import com.jayce.vexis.business.file.pool.FileContentsFragment
import com.jayce.vexis.business.history.HistoryFragment
import com.jayce.vexis.business.kit.KitFragment
import com.jayce.vexis.business.map.MapFragment
import com.jayce.vexis.business.peer.PeerFragment
import com.jayce.vexis.business.role.dashboard.AvatarSignnature
import com.jayce.vexis.business.role.dashboard.DashboardActivity
import com.jayce.vexis.business.setting.SettingActivity
import com.jayce.vexis.core.SessionManager.BASE_FILE_PATH
import com.jayce.vexis.core.SessionManager.user
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.core.base.BaseActivity.ActivityCollector.finishAll
import com.jayce.vexis.databinding.ActivityMainBinding
import com.jayce.vexis.databinding.DialogBinding
import com.jayce.vexis.foundation.ability.EventHandle.getUnreadSize
import com.jayce.vexis.foundation.dynamic.ModuleHelper
import com.jayce.vexis.foundation.view.block.FlexibleDialog
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var scanLauncher: ActivityResultLauncher<ScanOptions>
    private lateinit var avatarView: ImageView
    private lateinit var emailView: ImageView
    private lateinit var chatMsgView: ImageView
    private lateinit var emailBadge: Badge
    private lateinit var chatBadge: Badge
    private var viewHolder: ViewHolder? = null

    inner class ViewHolder(
        val feedbackFragment: FeedbackFragment,
        val kitFragment: KitFragment,
        val historyFragment: HistoryFragment,
        val articleFragment: ArticleFragment,
        val senior: PeerFragment,
        val fileFragment: FileFragment,
        val mapFragment: MapFragment
    )

    override fun registerLauncher() {
        scanLauncher = getLauncher(ScanContract()){
            val resultContent = it.contents
            if (resultContent.isNullOrBlank() || !resultContent.startsWith(URL_PREFIX)) {
                getString(R.string.invalid_qrcode).toast()
                return@getLauncher
            }
            resultContent.toast()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPage()
        //将顶部的间距设置为状态栏高度
        //将底部的间距设置为导航栏的高度
        //这样做的原因是使用了自定义的toolbar和 navigation组件
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = systemBars.top
                bottomMargin = navigationBars.bottom
            }
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        chatBadge.badgeNumber = getUnreadSize()
        val avatarTimestamp = readPrefs {
                getLong(AVATAR_SAVE_TIME, 0)
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
                viewHolder = ViewHolder(
                    FeedbackFragment(), KitFragment(), HistoryFragment(), ArticleFragment(),
                    PeerFragment(), FileFragment(), MapFragment()
                )
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
            replaceFragment(viewHolder!!.historyFragment, FRAGMENT_HISTORY)
            navigation.setNavigationItemSelectedListener { item ->
                toolBarText.text = item.title
                drawerLayout.closeDrawers()
                viewHolder?.apply {
                    when (item.itemId) {
                        R.id.MainMenuFeedback -> replaceFragment(feedbackFragment, FRAGMENT_FEEDBACK)
                        R.id.MainMenuWidget -> replaceFragment(kitFragment, FRAGMENT_KIT)
                        R.id.MainMenuTimeline -> replaceFragment(historyFragment, FRAGMENT_HISTORY)
                        R.id.MainMenuSynergy -> replaceFragment(articleFragment, FRAGMENT_ARTICLE)
                        R.id.MainMenuSenior -> replaceFragment(senior, FRAGMENT_SENIOR)
                        R.id.MainMenuResource -> replaceFragment(fileFragment, FRAGMENT_FILE)
//                        R.id.MainMenuMap -> replaceFragment(mapFragment, FRAGMENT_MAP)
                        R.id.MainMenuMap -> {
                            val fragment = ModuleHelper.getFragment("com.jayce.vexis.dynamic.ToolFragment")
                                            ?: mapFragment
                            fragment.apply {
                                replaceFragment(fragment, FRAGMENT_MAP)
                            }

                        }
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
                    override fun onDrawerSlide(drawerView: View, slideOffset: Float, ) { /**/ }
                    override fun onDrawerClosed(drawerView: View) { /**/ }
                    override fun onDrawerStateChanged(newState: Int) { /**/ }
                    override fun onDrawerOpened(drawerView: View) {
                        chatBadge.badgeNumber = getUnreadSize()
                        emailBadge.badgeNumber = 0
                    }
                }
            )
        }
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    FlexibleDialog<DialogBinding>(this@MainActivity, layoutInflater)
                        .flexibleView {
                            message.text = getString(R.string.confirm_exit)
                        }
                        .title(getString(R.string.message_hint))
                        .positive(getString(R.string.exist), true) {
                            return@positive 1
                        }
                        .positiveHandle{ flag,dia ->
                            if (flag == 1) {
                                finishAll()
                            }
                        }
                        .negative(getString(R.string.click_error), true) {
                            return@negative 1
                        }
                        .show()
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
            R.id.scanQRCode -> {
                scanLauncher.launch(ScanOptions().apply {
                    setPrompt(getString(R.string.begin_scan_qrcode))
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