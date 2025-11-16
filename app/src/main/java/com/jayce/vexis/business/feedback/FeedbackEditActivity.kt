package com.jayce.vexis.business.feedback

import android.os.Bundle
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.NetTool.sendNotifyMessage
import com.creezen.tool.ThreadTool
import com.creezen.tool.ThreadTool.getScope
import com.creezen.tool.ability.thread.BlockOption
import com.creezen.tool.ability.thread.ThreadType
import com.jayce.vexis.core.CoreService
import com.jayce.vexis.core.CoreService.Companion.NAME_MESSAGE_SCOPE
import com.jayce.vexis.core.SessionManager.user
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityFeedbackEditBinding
import com.jayce.vexis.foundation.Util
import com.jayce.vexis.foundation.route.FeedbackService
import kotlinx.coroutines.Dispatchers

class FeedbackEditActivity : BaseActivity<ActivityFeedbackEditBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        with(binding) {
            submit.setOnClickListener {
                val titleMsg = title.msg()
                val contentMsg = content.msg()
                val option = BlockOption(ThreadType.MULTI, 2000, Dispatchers.IO)
                ThreadTool.runWithBlocking(option) {
                    Util.request<FeedbackService, Boolean>({
                        sendFeedback(user().userId, titleMsg, contentMsg)
                    }) {
                        if (it) finish()
                    }
                }.onComplete {
                    getScope(NAME_MESSAGE_SCOPE)?.let {
                        sendNotifyMessage(it, contentMsg)
                    }
                }
            }
        }
    }
}