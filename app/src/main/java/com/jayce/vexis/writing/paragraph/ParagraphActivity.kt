package com.jayce.vexis.writing.paragraph

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.tool.NetTool.await
import com.creezen.tool.NetTool.create
import com.jayce.vexis.base.BaseActivity
import com.jayce.vexis.databinding.ActivityParagraphBinding
import com.jayce.vexis.writing.ArticleService
import kotlinx.coroutines.launch

class ParagraphActivity : BaseActivity() {
    private lateinit var binding: ActivityParagraphBinding
    private val paragraphList = arrayListOf<ParagraphCommandBean>()
    private var articleId: Long = -1
    private val adapter by lazy {
        ParagraphAdapter(this, this, paragraphList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParagraphBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPage()
        initData()
    }

    private fun initPage() {
        articleId = intent.getLongExtra("articleId", -1)
        with(binding) {
            paragraphRv.layoutManager = LinearLayoutManager(this@ParagraphActivity)
            paragraphRv.adapter = adapter
            adapter.setArticleId(articleId)
        }
    }

    private fun initData() {
        lifecycleScope.launch {
            val paragraphs =
                create<ArticleService>()
                    .getParagraphs(articleId)
                    ?.await()
                    ?: arrayListOf()
            paragraphList.addAll(paragraphs)
            adapter.notifyDataSetChanged()
        }
    }
}
