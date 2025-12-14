package com.jayce.vexis.business.article

import android.os.Bundle
import android.widget.TextView
import com.creezen.tool.AndroidTool.msg
import com.jayce.vexis.core.SessionManager.user
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivitySynergyEditBinding
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.route.ArticleService
import java.util.ArrayList

class ArticleEditActivity : BaseActivity<ActivitySynergyEditBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPage()
    }

    private fun initPage() {
        binding.submit.setOnClickListener {
            val title = binding.title.msg(true)
            val paragraphs = getParagraphList(binding.content)
            request<ArticleService, Boolean>({
                postSynergy(title, paragraphs, user().userId)
            }) {
                if (it) {
                    finish()
                }
            }
        }
    }

    private fun getParagraphList(textView: TextView): ArrayList<String> {
        val msg = textView.msg(true)
        val paragraphSequence = msg.split("\n")
            .asSequence()
            .filterNot {
                it.isEmpty()
            }
        val list = arrayListOf<String>()
        list.addAll(paragraphSequence.toList())
        return list
    }
}