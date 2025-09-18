package com.jayce.vexis.business.article.paragraph

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.commontool.bean.SectionRemarkBean
import com.creezen.tool.NetTool.await
import com.creezen.tool.NetTool.create
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityParagraphBinding
import com.jayce.vexis.foundation.route.ArticleService
import kotlinx.coroutines.launch

class ParagraptActivity : BaseActivity<ActivityParagraphBinding>() {

    private val paragraphList = arrayListOf<SectionRemarkBean>()
    private var articleId: Long = -1
    private val adapter by lazy {
        ParaAdapter(this, this, paragraphList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPage()
        initData()
    }

    private fun initPage() {
        articleId = intent.getLongExtra("articleId", -1)
        with(binding) {
            paragraphRv.layoutManager = LinearLayoutManager(this@ParagraptActivity)
            paragraphRv.adapter = adapter
            adapter.setArticleId(articleId)
        }
    }

    private fun initData() {
        lifecycleScope.launch {
            val paragraphs = create<ArticleService>()
                    .getParagraphs(articleId)
                    ?.await()
                    ?: arrayListOf()
            paragraphList.addAll(paragraphs)
            adapter.notifyDataSetChanged()
        }
    }
}