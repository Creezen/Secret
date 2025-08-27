package com.jayce.vexis.business.member.manage

import androidx.lifecycle.lifecycleScope
import android.os.Build
import android.os.Bundle
import com.creezen.tool.NetTool.await
import com.creezen.tool.NetTool.create
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityActiveDataBinding
import com.jayce.vexis.foundation.bean.ActiveEntry
import com.jayce.vexis.foundation.route.UserService
import com.jayce.vexis.core.base.BaseViewModel
import kotlinx.coroutines.launch

class ActiveDataActivity : BaseActivity<BaseViewModel>() {

    private lateinit var binding: ActivityActiveDataBinding
    private var activeEntry: ActiveEntry? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActiveDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activeEntry = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("activeEntry", ActiveEntry::class.java)
        } else {
            intent.getParcelableExtra("activeEntry")
        }
        initPage()
    }

    private fun initPage() {
        with(binding) {
            activeEntry?.let {
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
                        activeEntry?.let {
                            manageUser(1, it.userID)
                        }
                    }
                }
                delete.setOnClickListener {
                    lifecycleScope.launch {
                        activeEntry?.let {
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
        activeEntry?.let {
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