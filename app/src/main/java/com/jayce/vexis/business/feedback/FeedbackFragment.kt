package com.jayce.vexis.business.feedback

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.commontool.bean.FeedbackBean
import com.jayce.vexis.R
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.ActivityFeedbackBinding
import com.jayce.vexis.domain.route.FeedbackService
import com.jayce.vexis.foundation.Util.request

class FeedbackFragment : BaseFragment<ActivityFeedbackBinding>() {

    private val feedbackEntryList = arrayListOf<FeedbackBean>()
    private val feedbackAdapter by lazy { FeedBackAdapter(feedbackEntryList) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        initView()
        return binding.root
    }

    override fun onGetData(firstInit: Boolean) {
        super.onGetData(firstInit)
        updateData()
    }

    private fun initView() = binding.apply {
        floatingBtn.setOnClickListener {
            startActivity(Intent(activity, FeedbackEditActivity::class.java))
        }

        refreshLayout.setLoadingColors(R.color.metallicGold, R.color.vermilion)
        refreshLayout.setMaxOffset(100)
        refreshLayout.setTriggerDistance(300)
        refreshLayout.setOnRefreshListener {
            updateData()
        }
        refreshLayout.layoutManager = LinearLayoutManager(context)
        refreshLayout.adapter = feedbackAdapter
    }

    private fun updateData() {
        request<FeedbackService, ArrayList<FeedbackBean>>({ getFeedback() }) {
            feedbackAdapter.notifyDataChange(it)
            binding.refreshLayout.isRefreshing = false
        }
    }
}