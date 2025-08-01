package com.jayce.vexis.business.member.manage

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.tool.NetTool
import com.creezen.tool.NetTool.await
import com.jayce.vexis.foundation.base.BaseActivity
import com.jayce.vexis.databinding.ActivityAdminBinding
import com.jayce.vexis.business.member.ActiveItem
import com.jayce.vexis.business.member.UserService
import com.jayce.vexis.foundation.base.BaseViewModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdminActivity : BaseActivity<BaseViewModel>() {

    private lateinit var binding: ActivityAdminBinding
    private val userList = arrayListOf<ActiveItem>()
    private val adapter by lazy {
        UserActiveAdapter(this, userList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPage()
        initData()
    }

    private fun initPage() {
        with(binding) {
            listRV.layoutManager = LinearLayoutManager(this@AdminActivity)
            listRV.adapter = adapter
        }
    }

    private fun initData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val remoteList = NetTool.create<UserService>()
                    .getAllUser()
                    .await()
            val remoteRes = remoteList["userActiveData"] ?: listOf()
            val originSize = userList.size
            userList.addAll(remoteRes)
            Log.e("AdminActivity.initData", "$userList")
            launch(Dispatchers.Main) {
                adapter.notifyItemRangeInserted(originSize, remoteRes.size)
            }
        }
    }
}