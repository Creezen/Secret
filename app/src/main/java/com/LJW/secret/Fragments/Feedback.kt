package com.ljw.secret.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ljw.secret.activities.FeedbackEditActivity
import com.ljw.secret.adapter.FeedbackAdapter
import com.ljw.secret.bean.FeedbackItem
import com.ljw.secret.databinding.ActivityFeedbackBinding
import com.ljw.secret.network.FeedbackService
import com.ljw.secret.util.NetworkServerCreator
import com.ljw.secret.util.await
import kotlinx.coroutines.launch

class Feedback:BaseFragment() {

    private lateinit var binding: ActivityFeedbackBinding
    private val feedbackItemList = arrayListOf<FeedbackItem>()
    private val feedbackAdapter by lazy {
        FeedbackAdapter(feedbackItemList)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ActivityFeedbackBinding.inflate(inflater)
        initView()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        updateData()
    }

    private fun initView() {
        with(binding){
            floatingBtn.setOnClickListener {
                startActivity(Intent(activity, FeedbackEditActivity::class.java))
            }
            recycleView.layoutManager = LinearLayoutManager(context)
            recycleView.adapter = feedbackAdapter
        }
    }

    private fun updateData() {
        lifecycleScope.launch {
            val feedbackRes = NetworkServerCreator.create<FeedbackService>()
                .getFeedback()
                .await()
            val list = feedbackRes["items"] ?: arrayListOf()
            feedbackItemList.clear()
            feedbackItemList.addAll(list)
            feedbackAdapter.notifyDataSetChanged()
        }
    }
}