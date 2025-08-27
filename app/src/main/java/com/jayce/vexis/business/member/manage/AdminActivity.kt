package com.jayce.vexis.business.member.manage

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.internal.LinkedTreeMap
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityAdminBinding
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.bean.ActiveEntry
import com.jayce.vexis.foundation.route.UserService
import com.jayce.vexis.core.base.BaseViewModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AdminActivity : BaseActivity<BaseViewModel>() {

    private lateinit var binding: ActivityAdminBinding
    private val userList = arrayListOf<ActiveEntry>()
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
        request<UserService, LinkedTreeMap<String, List<ActiveEntry>>>({
            getAllUser()
        }) {
            val remoteRes = it["userActiveData"] ?: listOf()
            val originSize = userList.size
            userList.addAll(remoteRes)
            Log.e("AdminActivity.initData", "$userList")
            withContext(Dispatchers.Main) {
                adapter.notifyItemRangeInserted(originSize, remoteRes.size)
            }
        }
    }
}