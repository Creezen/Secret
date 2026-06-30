package com.jayce.vexis.business.feedback

import android.os.Bundle
import com.jayce.vexis.client.AndroidTool.msg
import com.jayce.vexis.client.NetTool.sendFeedbackMessage
import com.jayce.vexis.client.ThreadTool
import com.jayce.vexis.client.ThreadTool.getScope
import com.jayce.vexis.client.ability.thread.BlockOption
import com.jayce.vexis.client.ability.thread.ThreadType
import com.jayce.vexis.StatusManager.liveUser
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityFeedbackEditBinding
import com.jayce.vexis.domain.route.FeedbackService
import com.jayce.vexis.foundation.Util
import com.jayce.vexis.foundation.ability.EventRepository.Companion.SCOPE_EVENT
import com.jayce.vexis.util.getRandomString
import kotlinx.coroutines.Dispatchers

class FeedbackEditActivity : BaseActivity<ActivityFeedbackEditBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() = binding.apply {
        submit.setOnClickListener {
            val title = title.msg()
            val content = content.msg()
            val option = BlockOption(ThreadType.MULTI, 2000, Dispatchers.IO)
            ThreadTool.runWithBlocking(option) {
                Util.request<FeedbackService, Boolean>({
                    sendFeedback(getRandomString(7), liveUser.userId, title, content, "NORMAL")
                }) { if (it) finish() }
            }.onComplete {
                getScope(SCOPE_EVENT)?.let {
                    sendFeedbackMessage(it, title)
                }
            }
        }
    }
}