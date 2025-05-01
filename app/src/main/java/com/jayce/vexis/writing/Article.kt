package com.jayce.vexis.writing

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.tool.NetTool.await
import com.creezen.tool.NetTool.create
import com.creezen.tool.ThreadTool
import com.jayce.vexis.R
import com.jayce.vexis.base.BaseFragment
import com.jayce.vexis.databinding.FragmentSynergyBinding
import kotlinx.coroutines.Dispatchers

class Article : BaseFragment() {
    private lateinit var binding: FragmentSynergyBinding
    private val list = arrayListOf<ArticleBean>()
    private val adapter by lazy {
        ArticleAdapter(requireActivity(), list)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSynergyBinding.inflate(inflater)
        initView()
        initData()
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

    private fun initData() {
        ThreadTool.runOnMulti(Dispatchers.Main) {
            val result =
                create<ArticleService>()
                    .getArticle()
                    ?.await() ?: arrayListOf()
            list.clear()
            list.addAll(result)
            adapter.notifyDataSetChanged()
        }
    }
}
