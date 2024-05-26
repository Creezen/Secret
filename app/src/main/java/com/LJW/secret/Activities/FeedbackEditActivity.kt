package com.ljw.secret.activities

import android.os.Bundle
import android.util.Log
import com.ljw.secret.databinding.ActivityFeedbackEditBinding

class FeedbackEditActivity:BaseActivity() {

    private lateinit var binding: ActivityFeedbackEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        with(binding){

            Log.e("FeedbackEditActivity.initView","click")
        }
    }
}