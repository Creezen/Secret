package com.jayce.vexis.critique

import android.os.Bundle
import android.util.Log
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.AndroidTool.workInDispatch
import com.creezen.tool.NetTool.await
import com.creezen.tool.NetTool.create
import com.creezen.tool.NetTool.sendNotifyMessage
import com.creezen.tool.contract.LifecycleJob
import com.jayce.vexis.base.BaseActivity
import com.jayce.vexis.databinding.ActivityFeedbackEditBinding
import com.jayce.vexis.onlineUser

class FeedbackEditActivity: BaseActivity() {

    private lateinit var binding: ActivityFeedbackEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        with(binding){
            submit.setOnClickListener {
                val titleMsg = title.msg()
                val contentMsg = content.msg()
                workInDispatch(this@FeedbackEditActivity, 3000, lifecycleJob = object: LifecycleJob{
                    override suspend fun onDispatch() {
                        val result = create<FeedbackService>()
                            .sendFeedback(onlineUser.userId, titleMsg, contentMsg)
                            .await()
                        if(result) { finish() }
                    }

                    override fun onTimeoutFinish(isWorkFinished: Boolean) {
                        if(isWorkFinished) {
                            sendNotifyMessage(this@FeedbackEditActivity, contentMsg)
                        }
                    }
                })
            }
            Log.e("FeedbackEditActivity.initView","click")
        }
    }
}