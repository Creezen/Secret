package com.jayce.vexis.business.article.render

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.jayce.vexis.client.ThreadTool
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivitySectionBinding
import com.jayce.vexis.domain.route.ArticleService
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.util.vo.SectionRemarkVO

class RenderActivity : BaseActivity<ActivitySectionBinding>() {

    private val sectionList = arrayListOf<SectionRemarkVO>()
    private var articleId: Long = -1
    private var articleTitle: String = ""
    private val adapter by lazy { RenderAdapter(this, this, sectionList) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        articleId = intent.getLongExtra("articleId", -1)
        articleTitle = intent.getStringExtra("articleTitle") ?: ""
        initPage()
        initData()
    }

    private fun initPage() = binding.apply {
        sectionRv.layoutManager = LinearLayoutManager(this@RenderActivity)
        sectionRv.adapter = adapter
        adapter.setArticleId(articleId)
        title.text = articleTitle
    }

    private fun initData() {
        ThreadTool.runOnMulti {
            request<ArticleService, _>({ getSection(articleId) }) {
                sectionList.addAll(it)
                adapter.notifyDataSetChanged()
            }
        }
    }
}