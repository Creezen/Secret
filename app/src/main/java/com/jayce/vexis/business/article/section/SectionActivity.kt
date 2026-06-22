package com.jayce.vexis.business.article.section

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.jayce.vexis.util.bean.SectionRemarkBean
import com.jayce.vexis.client.ThreadTool
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityParagraphBinding
import com.jayce.vexis.domain.route.ArticleService
import com.jayce.vexis.foundation.Util

class SectionActivity : BaseActivity<ActivityParagraphBinding>() {

    private val paragraphList = arrayListOf<SectionRemarkBean>()
    private var articleId: Long = -1
    private val adapter by lazy {
        SectionAdapter(this, this, paragraphList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        articleId = intent.getLongExtra("articleId", -1)
        initPage()
        initData()
    }

    private fun initPage() = binding.apply {
        paragraphRv.layoutManager = LinearLayoutManager(this@SectionActivity)
        paragraphRv.adapter = adapter
        adapter.setArticleId(articleId)
    }

    private fun initData() {
        ThreadTool.runOnMulti {
            Util.request<ArticleService, ArrayList<SectionRemarkBean>>({
                getSection(articleId)
            }) {
                paragraphList.addAll(it)
                adapter.notifyDataSetChanged()
            }
        }
    }
}