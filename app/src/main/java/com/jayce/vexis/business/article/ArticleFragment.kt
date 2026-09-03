package com.jayce.vexis.business.article

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.jayce.vexis.R
import com.jayce.vexis.business.article.edit.EditActivity
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.FragmentArticleBinding
import com.jayce.vexis.domain.route.ArticleService
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.ui.animator.RecycleItemAnimator
import com.jayce.vexis.util.vo.ArticleVO

class ArticleFragment : BaseFragment<FragmentArticleBinding>() {

    private val list = arrayListOf<ArticleVO>()
    private val adapter by lazy { ArticleAdapter(requireActivity(), list) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        initView()
        return binding.root
    }

    private fun initView() = binding.apply {
        articleRV.layoutManager = LinearLayoutManager(requireActivity())
        this@ArticleFragment.context?.let {
            articleRV.itemAnimator = RecycleItemAnimator(it)
        }
        adapter.cornerRadius = 12f
        articleRV.adapter = adapter
        adapter.setOnDelete { fetchData() }
        post.setOnClickListener {
            startActivity(Intent(activity, EditActivity::class.java))
            activity?.overridePendingTransition(
                R.anim.edit_activity_enter,
                R.anim.edit_activity_close,
            )
        }
    }

    override fun onGetData(firstInit: Boolean) {
        super.onGetData(firstInit)
        fetchData()
    }

    private fun fetchData() = request<ArticleService, _>({ getArticle() }) { adapter.notifyDataChange(it) }
}