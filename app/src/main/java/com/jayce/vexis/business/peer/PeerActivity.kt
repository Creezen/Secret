package com.jayce.vexis.business.peer

import android.os.Bundle
import com.jayce.vexis.client.AndroidTool.msg
import com.jayce.vexis.client.AndroidTool.toast
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityPeerBinding
import com.jayce.vexis.domain.route.PeerService
import com.jayce.vexis.foundation.Util.Extension.onFalse
import com.jayce.vexis.foundation.Util.Extension.onTrue
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.util.Config.NIL
import com.jayce.vexis.util.dto.PeerDTO

class PeerActivity : BaseActivity<ActivityPeerBinding>() {

    private var discipline: String = NIL
    private var major: String = NIL
    private var track: String = NIL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initPage()
    }

    private fun initData() {
        discipline = intent.getStringExtra("discipline") ?: NIL
        major = intent.getStringExtra("major") ?: NIL
        track = intent.getStringExtra("track") ?: NIL
    }

    private fun initPage() = binding.apply {
        category.text = "TO $discipline $major ${track}"
        content.hint = "请留言"
        submit.setOnClickListener {
            val text = content.msg(true)
            if (text.isBlank()) {
                "内容不可以为空哦！".toast()
                return@setOnClickListener
            }
            val bean = PeerDTO(discipline, major, track, text)
            request<PeerService, _>({ sendSeniorAdvice(bean) }) {
                it.onTrue { finish() }.onFalse { "服务器错误，请重试!!".toast() }
            }
        }
    }
}