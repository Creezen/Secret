package com.jayce.vexis.business.feedback

import android.os.Bundle
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.AndroidTool.workInDispatch
import com.creezen.tool.NetTool.await
import com.creezen.tool.NetTool.create
import com.creezen.tool.NetTool.sendNotifyMessage
import com.creezen.tool.contract.LifecycleJob
import com.jayce.vexis.core.CoreService
import com.jayce.vexis.core.SessionManager.user
import com.jayce.vexis.foundation.base.BaseActivity
import com.jayce.vexis.databinding.ActivityFeedbackEditBinding

class FeedbackEditActivity : BaseActivity() {

    private lateinit var binding: ActivityFeedbackEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        with(binding) {
            submit.setOnClickListener {
                val titleMsg = title.msg()
                val contentMsg = content.msg()
                workInDispatch(
                    this@FeedbackEditActivity,
                    3000,
                    lifecycleJob = object : LifecycleJob {
                            override suspend fun onDispatch() {
                                val result = create<FeedbackService>()
                                        .sendFeedback(user().userId, titleMsg, contentMsg)
                                        .await()
                                if (result) {
                                    finish()
                                }
                            }

                            override fun onTimeoutFinish(isWorkFinished: Boolean) {
                                if (isWorkFinished) {
                                    sendNotifyMessage(CoreService.scope, contentMsg)
                                }
                            }
                        },
                )
            }
        }
    }
}