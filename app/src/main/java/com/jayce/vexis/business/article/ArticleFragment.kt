package com.jayce.vexis.business.article

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.jayce.vexis.util.bean.ArticleBean
import com.jayce.vexis.R
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.FragmentSynergyBinding
import com.jayce.vexis.domain.route.ArticleService
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.ui.animator.RecycleItemAnimator

class ArticleFragment : BaseFragment<FragmentSynergyBinding>() {

    private val list = arrayListOf<ArticleBean>()
    private val adapter by lazy { ArticleAdapter(requireActivity(), list) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        initView()
        return binding.root
    }

    private fun initView() = binding.apply {
        paragraphRv.layoutManager = LinearLayoutManager(requireActivity())
        this@ArticleFragment.context?.let {
            paragraphRv.itemAnimator = RecycleItemAnimator(it)
        }
        adapter.cornerRadius = 12f
        paragraphRv.adapter = adapter
        post.setOnClickListener {
            startActivity(Intent(activity, ArticleEditActivity::class.java))
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