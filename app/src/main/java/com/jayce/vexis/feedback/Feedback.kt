package com.jayce.vexis.feedback

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.tool.NetTool
import com.creezen.tool.NetTool.await
import com.jayce.vexis.base.BaseFragment
import com.jayce.vexis.databinding.ActivityFeedbackBinding
import kotlinx.coroutines.launch

class Feedback : BaseFragment() {

    companion object {
        const val TAG = "Feedback"
    }

    private lateinit var binding: ActivityFeedbackBinding
    private val feedbackItemList = arrayListOf<FeedbackItem>()
    private val feedbackAdapter by lazy {
        FeedbackAdapter(requireActivity(), feedbackItemList)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = ActivityFeedbackBinding.inflate(inflater)
        initView()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        updateData()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden) {
            updateData()
        }
    }

    private fun initView() {
        with(binding) {
            floatingBtn.setOnClickListener {
                startActivity(Intent(activity, FeedbackEditActivity::class.java))
            }
            recycleView.layoutManager = LinearLayoutManager(context)
            recycleView.adapter = feedbackAdapter
        }
    }

    private fun updateData() {
        lifecycleScope.launch {
            val feedbackRes =
                NetTool.create<FeedbackService>()
                    .getFeedback()
                    .await()
            val list = feedbackRes["items"] ?: arrayListOf()
            feedbackItemList.clear()
            feedbackItemList.addAll(list)
            feedbackAdapter.notifyItemRangeChanged(0, list.size)
        }
    }
}
