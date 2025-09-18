package com.jayce.vexis.business.role.manage

import androidx.lifecycle.lifecycleScope
import android.os.Build
import android.os.Bundle
import com.creezen.commontool.bean.ActiveBean
import com.creezen.tool.NetTool.await
import com.creezen.tool.NetTool.create
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityActiveDataBinding
import com.jayce.vexis.foundation.Util.Extension.unParcelable
import com.jayce.vexis.foundation.bean.ActiveEntry
import com.jayce.vexis.foundation.route.UserService
import kotlinx.coroutines.launch

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
                    lifecycleScope.launch {
                        activeBean?.let {
                            manageUser(1, it.userID)
                        }
                    }
                }
                delete.setOnClickListener {
                    lifecycleScope.launch {
                        activeBean?.let {
                            manageUser(2, it.userID)
                        }
                    }
                }
            }
        }
    }

    private suspend fun manageUser(
        operation: Int,
        userId: String,
    ) {
        activeBean?.let {
            lifecycleScope.launch {
                val awaitMap = create<UserService>()
                    .manageUser(operation, userId).await()
                val result = awaitMap["operationResult"]
                if (result == true) {
                    finish()
                }
            }
        }
    }
}