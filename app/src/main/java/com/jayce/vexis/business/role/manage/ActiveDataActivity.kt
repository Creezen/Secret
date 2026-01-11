package com.jayce.vexis.business.role.manage

import android.os.Build
import android.os.Bundle
import com.creezen.commontool.bean.ActiveBean
import com.creezen.tool.ThreadTool
import com.google.gson.internal.LinkedTreeMap
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityActiveDataBinding
import com.jayce.vexis.foundation.Util.Extension.unParcelable
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.bean.ActiveEntry
import com.jayce.vexis.foundation.route.UserService

class ActiveDataActivity : BaseActivity<ActivityActiveDataBinding>() {

    private var activeBean: ActiveBean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activeEntry = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("activeEntry", ActiveEntry::class.java)
        } else {
            intent.getParcelableExtra("activeEntry")
        }
        activeBean = activeEntry?.unParcelable()
        initPage()
    }

    private fun initPage() {
        with(binding) {
            activeBean?.let {
                nickname.text = it.nickname
                userID.text = it.userID
                createTime.text = it.createTime
                support.text = "${it.support}"
                against.text = "${it.against}"
                inform.text = "${it.inform}"
                reported.text = "${it.reported}"
                follow.text = "${it.follow}"
                fans.text = "${it.fans}"
                post.text = "${it.post}"
                setAdministrator.setOnClickListener {
                    activeBean?.userID?.let {
                        manageUser(1, it)
                    }
                }
                delete.setOnClickListener {
                    activeBean?.userID?.let {
                        manageUser(2, it)
                    }
                }
            }
        }
    }

    private fun manageUser(operation: Int, userId: String) {
        activeBean?.let {
            ThreadTool.runOnMulti {
                request<UserService, LinkedTreeMap<String, Boolean>>({
                    manageUser(operation, userId)
                }) {
                    val result = it["operationResult"]
                    if (result == true) finish()
                }
            }
        }
    }
}