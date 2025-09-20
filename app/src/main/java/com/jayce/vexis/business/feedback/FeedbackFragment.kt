package com.jayce.vexis.business.feedback

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.commontool.bean.FeedbackBean
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.ActivityFeedbackBinding
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.route.FeedbackService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeedbackFragment : BaseFragment<ActivityFeedbackBinding>() {

    companion object {
        const val TAG = "Feedback"
    }

    private val feedbackEntryList = arrayListOf<FeedbackBean>()
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
        request<FeedbackService, LinkedHashMap<String, ArrayList<FeedbackBean>>>({ getFeedback() }) {
            withContext(Dispatchers.Main) {
                val list = it["items"] ?: arrayListOf()
                feedbackEntryList.clear()
                feedbackEntryList.addAll(list)
                feedbackAdapter.notifyItemRangeChanged(0, list.size)
            }
        }
    }
}