package com.jayce.vexis.business.article

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.commontool.bean.ArticleBean
import com.jayce.vexis.R
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.FragmentSynergyBinding
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.route.ArticleService

class ArticleFragment : BaseFragment<FragmentSynergyBinding>() {

    private val list = arrayListOf<ArticleBean>()
    private val adapter by lazy {
        ArticleAdapter(requireActivity(), list)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        initView()
        return binding.root
    }

    private fun initView() {
        with(binding) {
            paragraphRv.layoutManager = LinearLayoutManager(requireActivity())
            paragraphRv.adapter = adapter
            post.setOnClickListener {
                startActivity(Intent(activity, ArticleEditActivity::class.java))
                activity?.overridePendingTransition(
                    R.anim.edit_activity_enter,
                    R.anim.edit_activity_close,

                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initData() {
        request<ArticleService, ArrayList<ArticleBean>>({ getArticle() }) {
            list.clear()
            adapter.notifyItemRemoved(0)
            list.addAll(it)
            adapter.notifyItemInserted(0)
        }
    }
}