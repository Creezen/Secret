package com.jayce.vexis.business.member.manage

import androidx.lifecycle.lifecycleScope
import android.os.Build
import android.os.Bundle
import com.creezen.tool.NetTool.await
import com.creezen.tool.NetTool.create
import com.jayce.vexis.foundation.base.BaseActivity
import com.jayce.vexis.databinding.ActivityActiveDataBinding
import com.jayce.vexis.business.member.ActiveItem
import com.jayce.vexis.business.member.UserService
import com.jayce.vexis.foundation.base.BaseViewModel
import kotlinx.coroutines.launch

class ActiveDataActivity : BaseActivity<BaseViewModel>() {

    private lateinit var binding: ActivityActiveDataBinding
    private var activeItem: ActiveItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActiveDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activeItem = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("activeItem", ActiveItem::class.java)
        } else {
            intent.getParcelableExtra("activeItem")
        }
        initPage()
    }

    private fun initPage() {
        with(binding) {
            activeItem?.let {
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
                        activeItem?.let {
                            manageUser(1, it.userID)
                        }
                    }
                }
                delete.setOnClickListener {
                    lifecycleScope.launch {
                        activeItem?.let {
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
        activeItem?.let {
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