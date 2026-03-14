package com.jayce.vexis.business.role.manage

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.commontool.bean.ActiveBean
import com.creezen.tool.ThreadTool.ui
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityAdminBinding
import com.jayce.vexis.domain.route.UserService
import com.jayce.vexis.foundation.Util.request

class AdminActivity : BaseActivity<ActivityAdminBinding>() {

    private val userList = arrayListOf<ActiveBean>()
    private val adapter by lazy {
        UserActiveAdapter(this, userList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPage()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initPage() {
        with(binding) {
            adapter.cardPadding = 6
            listRV.layoutManager = LinearLayoutManager(this@AdminActivity)
            listRV.adapter = adapter
        }
    }

    private fun initData() {
        request<UserService, List<ActiveBean>>({ getAllUser() }) {
            val originSize = userList.size
            userList.clear()
            adapter.notifyItemRangeRemoved(0, originSize)
            userList.addAll(it)
            ui { adapter.notifyItemRangeInserted(0, it.size) }
        }
    }
}