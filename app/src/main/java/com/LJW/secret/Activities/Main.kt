package com.LJW.secret.Activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.LJW.secret.Fragments.ChatBox
import com.LJW.secret.Fragments.Feedback
import com.LJW.secret.Fragments.ListPage
import com.LJW.secret.Fragments.TimeLine
import com.LJW.secret.Fragments.Widgets
import com.LJW.secret.Fragments.YQIA
import com.LJW.secret.R
import com.LJW.secret.databinding.ActivityMainBinding
import com.LJW.secret.replaceFragment

class Main : BaseActivity() {

    private lateinit var binding:ActivityMainBinding
    private var viewHolder: ViewHolder?=null
    inner class ViewHolder(val feedback: Feedback,val yqia: YQIA,val widgets: Widgets,
                           val listPage: ListPage,val chatBox: ChatBox,val timeLine: TimeLine)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPage()
    }

    fun initPage(){
        with(binding){
            if (null==viewHolder){
                viewHolder=ViewHolder(Feedback(),YQIA(), Widgets(), ListPage(), ChatBox(),
                    TimeLine()
                )
                root.tag=viewHolder
            }else viewHolder=root.tag as ViewHolder
            setSupportActionBar(toolBar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.open_drawer)
            }
            toolBarText.setText(navigation.menu.getItem(0).title)
            replaceFragment(viewHolder!!.yqia)
            navigation.setNavigationItemSelectedListener { item->
                toolBarText.setText(item.title)
                viewHolder?.apply {
                    when(item.itemId){
                        R.id.MainMenuFeedback -> replaceFragment(feedback)
                        R.id.MainMenuYouQIA -> replaceFragment(yqia)
                        R.id.MainMenuWidget -> replaceFragment(widgets)
                        R.id.MainMenuFunction -> replaceFragment(listPage)
                        R.id.MainMenuShare -> replaceFragment(chatBox)
                        R.id.MainMenuTimeline -> replaceFragment(timeLine)
                    }
                }

                drawerLayout.closeDrawers()
                return@setNavigationItemSelectedListener true
            }
        }
        onBackPressedDispatcher.addCallback(this,object:OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                AlertDialog.Builder(this@Main).apply {
                    setTitle("消息提醒")
                    setMessage("确定退出吗？")
                    setCancelable(false)
                    setPositiveButton("退出"){_,_->
                        finish()
                    }
                    setNegativeButton("点错了"){dialog,_->
                        dialog.dismiss()
                    }
                    show()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_tool_bar,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.MainLogout -> {

            }
            android.R.id.home -> binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
    }

    private fun replaceFragment(fragment: Fragment){
        replaceFragment(supportFragmentManager,R.id.frame,fragment,false)
    }
}