package com.jayce.vexis.business.feedback

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.ActivityFeedbackBinding
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.bean.FeedbackEntry
import com.jayce.vexis.foundation.route.FeedbackService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.ArrayList
import java.util.LinkedHashMap

class Feedback : BaseFragment<ActivityFeedbackBinding>() {

    companion object {
        const val TAG = "Feedback"
    }

    private val feedbackEntryList = arrayListOf<FeedbackEntry>()
    private val feedbackAdapter by lazy {
        FeedbackAdapter(requireActivity(), feedbackEntryList)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
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
        request<FeedbackService, LinkedHashMap<String, ArrayList<FeedbackEntry>>>({ getFeedback() }) {
            withContext(Dispatchers.Main) {
                val list = it["items"] ?: arrayListOf()
                feedbackEntryList.clear()
                feedbackEntryList.addAll(list)
                feedbackAdapter.notifyItemRangeChanged(0, list.size)
            }
        }
    }
}