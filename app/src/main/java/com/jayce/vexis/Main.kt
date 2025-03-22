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
import com.creezen.tool.AndroidTool.replaceFragment
import com.creezen.tool.BaseTool.env
import com.creezen.tool.Constant
import com.creezen.tool.NetTool
import com.creezen.tool.NetTool.sendAckMessage
import com.jayce.vexis.base.BaseActivity
import com.jayce.vexis.base.BaseActivity.ActivityCollector.finishAll
import com.jayce.vexis.chat.ChatActivity
import com.jayce.vexis.history.HistoricalAxis
import com.jayce.vexis.issue.Feedback
import com.jayce.vexis.databinding.ActivityMainBinding
import com.jayce.vexis.ability.event.EventHandler.dispatchEvent
import com.jayce.vexis.exchange.ExchangeActivity
import com.jayce.vexis.member.dashboard.AvatarSignnature
import com.jayce.vexis.member.dashboard.HomePage
import com.jayce.vexis.widgets.SimpleDialog
import com.jayce.vexis.writing.Article
import com.jayce.vexis.gadgets.Gadget

class Main : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private var viewHolder: ViewHolder? = null
    inner class ViewHolder(val feedback: Feedback, val gadget: Gadget,
                           val historicalAxis: HistoricalAxis, val article: Article)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPage()
        startService(Intent(this, CreezenService::class.java))
    }

    private fun initPage(){
        with(binding){
            if (null == viewHolder){
                viewHolder = ViewHolder(Feedback(), Gadget(), HistoricalAxis(), Article())
                root.tag = viewHolder
            } else viewHolder = root.tag as ViewHolder
            setSupportActionBar(toolBar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.open_drawer)
            }
            toolBarText.text = navigation.menu.getItem(0).title
            replaceFragment(viewHolder!!.historicalAxis)
            navigation.setNavigationItemSelectedListener { item->
                toolBarText.text = item.title
                drawerLayout.closeDrawers()
                viewHolder?.apply {
                    when(item.itemId){
                        R.id.MainMenuFeedback -> replaceFragment(feedback)
                        R.id.MainMenuWidget -> replaceFragment(gadget)
                        R.id.MainMenuTimeline -> replaceFragment(historicalAxis)
                        R.id.MainMenuSynergy -> replaceFragment(article)
                    }
                }
                return@setNavigationItemSelectedListener true
            }
            navigation.setCheckedItem(R.id.MainMenuTimeline)
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
            }
            R.id.chat -> {
                startActivity(Intent(this, ChatActivity::class.java))
            }
            R.id.hub -> {
                startActivity(Intent(this, ExchangeActivity::class.java))
            }
            android.R.id.home -> binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
    }

    private fun replaceFragment(fragment: Fragment){
        replaceFragment(supportFragmentManager,R.id.frame,fragment)
    }

    //    private fun test() {
//        val a = binding.longText
//        binding.button.setOnClickListener {
//            val textValue = binding.edit.text.toString()
//            val list = textValue.split("\n")
//                .asSequence()
//                .filterNot {
//                    it.isEmpty()
//                }.map {
//                    it.trim()
//                }.take(10)
//            list.forEach{
//                Log.e("test", "the value: $it")
//            }
//            RandomAccessFile("filepath", "r").use {
//                it.seek(it.filePointer)
//                Channels.newInputStream(it.channel).bufferedReader().useLines {
//
//                }
//            }
//            val file = File("ddd")
//
//            file.useLines {
//                it.filterNot {
//                    it.isEmpty()
//                }.map {
//                    it.trim()
//                }
//            }
//            a.longText = textValue
//            binding.tv.text = "${list.count()}"
//        }
//    }
}