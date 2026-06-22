package com.jayce.vexis.business.peer

import android.os.Bundle
import com.jayce.vexis.util.Config.NIL
import com.jayce.vexis.client.AndroidTool.msg
import com.jayce.vexis.client.AndroidTool.toast
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityPeerBinding
import com.jayce.vexis.domain.route.PeerService
import com.jayce.vexis.foundation.Util.request

class PeerActivity : BaseActivity<ActivityPeerBinding>() {

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
        category.text = "TO [$primaryKey $secondKey ${tertiaryKey}] 专业的同学"
        content.hint = "请留言"
        submit.setOnClickListener {
            val text = content.msg(true)
            if (text.isBlank()) {
                "内容不可以为空哦！".toast()
                return@setOnClickListener
            }
            request<PeerService, Boolean>({
                sendSeniorAdvice(primaryKey, secondKey, tertiaryKey, text)
            }) {
                if (it) {
                    finish()
                    return@request
                }
                "服务器错误，请重试!!".toast()
            }
        }
    }
}