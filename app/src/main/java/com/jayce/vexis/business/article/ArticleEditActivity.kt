package com.jayce.vexis.business.article

import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.NetTool.await
import com.creezen.tool.NetTool.create
import com.jayce.vexis.core.SessionManager.user
import com.jayce.vexis.foundation.base.BaseActivity
import com.jayce.vexis.databinding.ActivitySynergyEditBinding
import com.jayce.vexis.foundation.base.BaseViewModel
import com.jayce.vexis.foundation.route.ArticleService
import kotlinx.coroutines.launch

class ArticleEditActivity : BaseActivity<BaseViewModel>() {

    private lateinit var binding: ActivitySynergyEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySynergyEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPage()
    }

    private fun initPage() {
        binding.submit.setOnClickListener {
            val title = binding.title.msg(true)
            val paragraphs = getParagraphList(binding.content)
            lifecycleScope.launch {
                val uploadResult = create<ArticleService>()
                        .postSynergy(title, paragraphs, user().userId)
                        .await()
                if (uploadResult) {
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