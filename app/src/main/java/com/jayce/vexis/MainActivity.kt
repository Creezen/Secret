package com.jayce.vexis

import android.app.Notification
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.NotificationCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.jayce.vexis.StatusManager.isLogin
import com.jayce.vexis.StatusManager.liveUser
import com.jayce.vexis.StatusManager.registerUser
import com.jayce.vexis.business.article.article.ArticleFragment
import com.jayce.vexis.business.chat.ChatActivity
import com.jayce.vexis.business.feedback.FeedbackFragment
import com.jayce.vexis.business.file.FileSheetFragment
import com.jayce.vexis.business.history.HistoryFragment
import com.jayce.vexis.business.kit.KitFragment
import com.jayce.vexis.business.login.LoginActivity
import com.jayce.vexis.business.mail.MailActivity
import com.jayce.vexis.business.map.MapFragment
import com.jayce.vexis.business.peer.PeerFragment
import com.jayce.vexis.business.profile.dashboard.DashboardActivity
import com.jayce.vexis.business.setting.SettingActivity
import com.jayce.vexis.client.AndroidTool.getData
import com.jayce.vexis.client.AndroidTool.replaceFragment
import com.jayce.vexis.client.AndroidTool.toast
import com.jayce.vexis.client.BaseTool.envContext
import com.jayce.vexis.client.NetTool.destroySocket
import com.jayce.vexis.client.TLog
import com.jayce.vexis.client.ThreadTool.runOnIO
import com.jayce.vexis.client.ThreadTool.runOnMain
import com.jayce.vexis.client.ThreadTool.ui
import com.jayce.vexis.client.bean.FragmentAnimRes
import com.jayce.vexis.client.bean.ImageOption
import com.jayce.vexis.core.CoreService
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.core.base.BaseActivity.ActivityCollector.finishAll
import com.jayce.vexis.databinding.ActivityMainBinding
import com.jayce.vexis.foundation.Util.Extension.jumpTo
import com.jayce.vexis.foundation.Util.Extension.load
import com.jayce.vexis.foundation.Util.Extension.onFalse
import com.jayce.vexis.foundation.Util.Extension.onTrue
import com.jayce.vexis.foundation.ability.EventRepository
import com.jayce.vexis.foundation.ability.Logger
import com.jayce.vexis.foundation.dynamic.ModuleHelper
import com.jayce.vexis.foundation.ui.block.FlexibleDialog
import com.jayce.vexis.util.Config.AVATAR_SAVE_TIME
import com.jayce.vexis.util.Config.FRAGMENT_ARTICLE
import com.jayce.vexis.util.Config.FRAGMENT_FEEDBACK
import com.jayce.vexis.util.Config.FRAGMENT_FILE
import com.jayce.vexis.util.Config.FRAGMENT_HISTORY
import com.jayce.vexis.util.Config.FRAGMENT_KIT
import com.jayce.vexis.util.Config.FRAGMENT_MAP
import com.jayce.vexis.util.Config.FRAGMENT_SENIOR
import com.jayce.vexis.util.Config.URL_PREFIX
import com.jayce.vexis.util.bean.UserBean
import com.jayce.vexis.util.toBean
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import org.koin.android.ext.android.inject
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView

class MainActivity : BaseActivity<ActivityMainBinding>(), DrawerListener, OnNavigationItemSelectedListener {

    private lateinit var scanLauncher: ActivityResultLauncher<ScanOptions>
    private lateinit var loginLauncher: ActivityResultLauncher<Intent>
    private lateinit var avatarView: ImageView
    private lateinit var emailView: ImageView
    private lateinit var chatMsgView: ImageView
    private lateinit var emailBadge: Badge
    private lateinit var chatBadge: Badge
    private var fragmentHolder: FragmentHolder? = null
    private val repository by inject<EventRepository>()

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as CoreService.ConnectionBinder
            val notification = buildNotification()
            binder.showNotification(notification)
        }

        override fun onServiceDisconnected(name: ComponentName?) { /**/ }
    }

    override fun registerLauncher() {
        scanLauncher = getLauncher(ScanContract()) {
            val resultContent = it.contents
            if (resultContent.isNullOrBlank() || !resultContent.startsWith(URL_PREFIX)) {
                getString(R.string.invalid_qrcode).toast()
                return@getLauncher
            }
            resultContent.toast()
        }

        loginLauncher = getLauncher(startActivity()) {
            val result = it.data ?: return@getLauncher
            val data = result.getBooleanExtra("launchResult", false)
            if (!data) return@getLauncher
            val launchValue = result.getStringExtra("launchValue") ?: return@getLauncher
            val user = launchValue.toBean<UserBean>() ?: return@getLauncher
            isLogin = true
            refreshStatusUI()
            registerUser(user)
            bindService(Intent(this, CoreService::class.java), connection, BIND_AUTO_CREATE)
        }
    }

    @Logger(a = "Hello")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPage()
        adjustWindowMargin()
        apiTest()
    }

    /**
     * 将顶部的间距设置为状态栏高度;
     * 将底部的间距设置为导航栏的高度;
     * 这样做的原因是使用了自定义的toolbar和 navigation组件;
     */
    private fun adjustWindowMargin() {
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
        if (!isLogin) return
        runOnMain {
            val time = getData(AVATAR_SAVE_TIME, 0L)
            val imageUrl = "${liveUser.userId}.png"
            val key = time.toString()
            val option = ImageOption(true, key, "/head")
            avatarView.load(imageUrl, option)
        }
    }

    private fun refreshStatusUI() {
        supportActionBar?.apply {
            val drawableId = if (isLogin) R.drawable.open_drawer else R.drawable.logon
            setHomeAsUpIndicator(drawableId)
        }
        val lockMode = if (isLogin) LOCK_MODE_UNLOCKED else LOCK_MODE_LOCKED_CLOSED
        binding.drawerLayout.setDrawerLockMode(lockMode)
    }

    private fun initPage() {
        if (null == fragmentHolder) {
            fragmentHolder = FragmentHolder()
        }
        initMainContent()
        initNavigation()
        refreshStatusUI()
        binding.drawerLayout.addDrawerListener(this@MainActivity)
        onBackPressedDispatcher.addCallback {
            FlexibleDialog
                .flexibleViewNormal(this@MainActivity) {
                    message.text = getString(R.string.confirm_exit)
                }
                .title(R.string.message_hint)
                .positive(R.string.exist, true) {
                    destroySocket()
                    unbindService(connection)
                    finishAll()
                }
                .negative(R.string.click_error, true) { }
                .show()
        }
    }

    private fun initMainContent() {
        binding.apply {
            setSupportActionBar(toolBar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolBarText.text = navigation.menu.getItem(0).title
            fragmentHolder?.kitFragment?.let { replaceFragment(it, FRAGMENT_KIT) }
            toolBarText.setOnClickListener {
                val clickId = navigation.checkedItem?.itemId
                if (clickId != R.id.MainMenuTimeline) return@setOnClickListener
                val fragment = fragmentHolder?.historyFragment ?: return@setOnClickListener
                fragment.changeOptionPanel()
            }
        }
    }

    private fun initNavigation() {
        val navigation = binding.navigation
        navigation.setNavigationItemSelectedListener(this@MainActivity)
        navigation.setCheckedItem(R.id.MainMenuWidget)
        val headView = navigation.getHeaderView(0) as LinearLayout
        avatarView = headView.findViewById(R.id.avataView)
        emailView = headView.findViewById(R.id.myEmail)
        chatMsgView = headView.findViewById(R.id.myChatMsg)
        avatarView.setOnClickListener { jumpTo(DashboardActivity::class.java) }
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
        chatMsgView.setOnClickListener { jumpTo(ChatActivity::class.java) }
        emailView.setOnClickListener { jumpTo(MailActivity::class.java) }

        //  navigation.menu.iterator().forEachRemaining {
        //      if (it.title == "建言献策") {
        //          it.isVisible = false
        //      }
        //  }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_tool_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.scanQRCode -> {
                val options = ScanOptions().apply {
                    setPrompt(getString(R.string.begin_scan_qrcode))
                    setBeepEnabled(false)
                    setTimeout(5000)
                    setBarcodeImageEnabled(false)
                }
                scanLauncher.launch(options)
            }
            R.id.setting -> { jumpTo(SettingActivity::class.java) }
            android.R.id.home -> {
                isLogin.onTrue {
                    binding.drawerLayout.openDrawer(GravityCompat.START)
                }.onFalse {
                    FlexibleDialog.flexibleViewNormal(this@MainActivity) {
                        message.text = "登录解锁更多功能!!!"
                    }
                    .title("是否要登录？")
                    .positive("立即登录") {
                        loginLauncher.launch(Intent(this@MainActivity, LoginActivity::class.java))
                    }
                    .negative("暂不登录") {}
                    .show()
                }
            }
        }
        return true
    }

    override fun onDrawerOpened(drawerView: View) {
        runOnIO {
            val emailNumber = repository.getUnreadMailCount()
            val chatNumber = repository.getUnreadChatCount()
            ui {
                emailBadge.badgeNumber = emailNumber
                chatBadge.badgeNumber = chatNumber
            }
        }.onFailure {
            TLog.d("onDrawerOpened failed: ${it.message}")
        }
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) { /**/ }

    override fun onDrawerClosed(drawerView: View) { /**/ }

    override fun onDrawerStateChanged(newState: Int) { /**/ }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        binding.toolBarText.text = item.title
        binding.drawerLayout.closeDrawers()
        fragmentHolder?.apply {
            val pair = getFragment(item.itemId)
            replaceFragment(pair.first, pair.second)
        }
        return true
    }

    private fun buildNotification(): Notification {
        val notification = NotificationCompat.Builder(envContext, "login")
            .setSmallIcon(R.mipmap.tianji)
            .setContentTitle(getString(R.string.login_success_notify))
            .setContentText(getString(R.string.welcome_user, liveUser.nickname))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setOngoing(true)
            .build()
        return notification
    }

    private fun replaceFragment(fragment: Fragment, fragmentTag: String) {
        val animRes = FragmentAnimRes(
            R.anim.fragment_enter,
            R.anim.fragment_exit,
            R.anim.fragment_pop_enter,
            R.anim.fragment_pop_exit
        )
        replaceFragment(supportFragmentManager, R.id.frame, fragment, fragmentTag, false, animRes)
    }

    inner class FragmentHolder {
        val feedbackFragment: FeedbackFragment = FeedbackFragment()
        val kitFragment: KitFragment = KitFragment()
        val historyFragment: HistoryFragment = HistoryFragment()
        val articleFragment: ArticleFragment = ArticleFragment()
        val senior: PeerFragment = PeerFragment()
        val fileSheetFragment: FileSheetFragment = FileSheetFragment()
        val mapFragment: MapFragment = MapFragment()

        fun getFragment(id: Int): Pair<Fragment, String> = when (id) {
            R.id.MainMenuFeedback -> feedbackFragment to FRAGMENT_FEEDBACK
            R.id.MainMenuWidget -> kitFragment to FRAGMENT_KIT
            R.id.MainMenuTimeline -> historyFragment to FRAGMENT_HISTORY
            R.id.MainMenuSynergy -> articleFragment to FRAGMENT_ARTICLE
            R.id.MainMenuSenior -> senior to FRAGMENT_SENIOR
            R.id.MainMenuResource -> fileSheetFragment to FRAGMENT_FILE
            R.id.MainMenuMap -> {
                val moduleName = "com.jayce.vexis.dynamic.ToolFragment"
                val fragment = ModuleHelper.getFragment(moduleName) ?: mapFragment
                fragment to FRAGMENT_MAP
            }
            else -> kitFragment to FRAGMENT_KIT
        }
    }

    private fun apiTest() { /**/ }
}