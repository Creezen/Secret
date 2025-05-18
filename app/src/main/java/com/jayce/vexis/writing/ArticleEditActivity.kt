package com.jayce.vexis.writing

import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.NetTool.await
import com.creezen.tool.NetTool.create
import com.jayce.vexis.Constant.PARAGRAPH_HEAD
import com.jayce.vexis.base.BaseActivity
import com.jayce.vexis.databinding.ActivitySynergyEditBinding
import com.jayce.vexis.onlineUser
import kotlinx.coroutines.launch

class ArticleEditActivity : BaseActivity() {
    private lateinit var binding: ActivitySynergyEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySynergyEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPage()
    }

    private fun initPage() {
        binding.submit.setOnClickListener {
            val paragraphs = getParagraphList(binding.content)
            lifecycleScope.launch {
                val uploadResult =
                    create<ArticleService>()
                        .postSynergy(paragraphs, onlineUser.userId)
                        .await()
                if (uploadResult) {
                    finish()
                }
            }
        }
    }

    private fun getParagraphList(textView: TextView): ArrayList<String> {
        val msg = textView.msg(true)
        val paragraphSequence =
            msg.split("\n")
                .asSequence()
                .filterNot {
                    it.isEmpty()
                }.map {
                    "$PARAGRAPH_HEAD${it.trim()}"
                }
        val list = arrayListOf<String>()
        list.addAll(paragraphSequence.toList())
        return list
    }
}
