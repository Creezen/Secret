package com.jayce.vexis.business.peer

import android.os.Bundle
import android.util.Log
import com.creezen.commontool.Config.NIL
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.ThreadTool.ui
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityAdviceBinding
import com.jayce.vexis.domain.route.PeerService
import com.jayce.vexis.foundation.Util.request

class AdviceActivity : BaseActivity<ActivityAdviceBinding>() {

    companion object {
        const val TAG = "AdviceActivity"
    }

    private var primaryKey: String = NIL
    private var secondKey: String = NIL
    private var tertiaryKey: String = NIL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initPage()
    }

    private fun initData() {
        primaryKey = intent.getStringExtra("primary") ?: NIL
        secondKey = intent.getStringExtra("secord") ?: NIL
        tertiaryKey = intent.getStringExtra("tertiary") ?: NIL
    }

    private fun initPage() = binding.apply {
        content.hint = "留下你对$primaryKey/$secondKey/${tertiaryKey}专业的同学的话吧！"
        submit.setOnClickListener {
            val text = content.msg(true)
            if (text.isBlank()) {
                "内容不可以为空哦！".toast()
                return@setOnClickListener
            }
            Log.d(TAG, "send: $primaryKey/$secondKey/$tertiaryKey")
            request<PeerService, Boolean>({
                sendSeniorAdvice(primaryKey, secondKey, tertiaryKey, text)
            }) {
                if (it) {
                    finish()
                    return@request
                }
                ui { "服务器错误，请重试!!".toast() }
            }
        }
    }
}