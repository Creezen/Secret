package com.jayce.vexis

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.creezen.tool.AndroidTool.readPrefs
import com.jayce.vexis.base.BaseActivity.ActivityCollector.finishAll
import com.jayce.vexis.chat.ChatActivity
import com.jayce.vexis.chronicle.Chronicle
import com.jayce.vexis.member.dashboard.HomePage
import com.jayce.vexis.databinding.ActivityMainBinding
import com.jayce.vexis.critique.Feedback
import com.jayce.vexis.hub.HubActivity
import com.jayce.vexis.stylized.SimpleDialog
import com.jayce.vexis.utility.Utility
import com.creezen.tool.AndroidTool.replaceFragment
import com.creezen.tool.BaseTool.env
import com.creezen.tool.Constant
import com.creezen.tool.NetTool
import com.jayce.vexis.base.BaseActivity
import com.jayce.vexis.login.Login.Companion.setLoginStatus
import com.jayce.vexis.member.dashboard.AvatarSignnature
import com.jayce.vexis.synergy.Synergy

class Main : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private var viewHolder: ViewHolder? = null
    inner class ViewHolder(val feedback: Feedback, val utility: Utility,
                           val chronicle: Chronicle, val synergy: Synergy)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPage()
        showNotify()
    }

    private fun showNotify() {
        val notifyChannel = NotificationChannel("1", "login", NotificationManager.IMPORTANCE_HIGH)
        val builder = NotificationCompat.Builder(env(), "1")
            .setSmallIcon(R.drawable.tianji)
            .setContentTitle("登录成功通知")
            .setContentText("欢迎您，${onlineUser.nickname}")
            .build()
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(notifyChannel)
        manager.notify(1, builder)
    }

    private fun initPage(){
        with(binding){
            if (null == viewHolder){
                viewHolder = ViewHolder(Feedback(), Utility(), Chronicle(), Synergy())
                root.tag = viewHolder
            } else viewHolder = root.tag as ViewHolder
            setSupportActionBar(toolBar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.open_drawer)
            }
            toolBarText.text = navigation.menu.getItem(0).title
            replaceFragment(viewHolder!!.chronicle)
            navigation.setNavigationItemSelectedListener { item->
                toolBarText.text = item.title
                drawerLayout.closeDrawers()
                viewHolder?.apply {
                    when(item.itemId){
                        R.id.MainMenuFeedback -> replaceFragment(feedback)
                        R.id.MainMenuWidget -> replaceFragment(utility)
                        R.id.MainMenuTimeline -> replaceFragment(chronicle)
                        R.id.MainMenuSynergy -> replaceFragment(synergy)
                    }
                }
                return@setNavigationItemSelectedListener true
            }
            navigation.getHeaderView(0).setOnClickListener {
                startActivity(Intent(this@Main, HomePage::class.java))
            }
            val avatarTimestamp = readPrefs {
                it.getLong("cursorTime", 0)
            }
            val headView = navigation.getHeaderView(0) as ImageView
            NetTool.setImage(
                this@Main,
                headView,
                "${Constant.BASE_FILE_PATH}head/${onlineUser.userId}.png",
                key = AvatarSignnature("key:$avatarTimestamp"),
                isCircle = true
            )
        }
        onBackPressedDispatcher.addCallback(this, object:OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                SimpleDialog(this@Main).apply {
                    setTitle("消息提醒")
                    setMessage("确定退出吗？")
                    setLeftButton("点错了"){ _, dialog ->
                        dialog.dismiss()
                    }
                    setRightButton("退出"){ _, _ ->
                        dismiss()
                        finishAll()
                        setLoginStatus(false)
                    }
                    show()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_tool_bar, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout -> {
                finishAll()
                setLoginStatus(false)
            }
            R.id.chat -> {
                startActivity(Intent(this, ChatActivity::class.java))
            }
            R.id.hub -> {
                startActivity(Intent(this, HubActivity::class.java))
            }
            android.R.id.home -> binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
    }

    private fun replaceFragment(fragment: Fragment){
        replaceFragment(supportFragmentManager,R.id.frame,fragment)
    }
}