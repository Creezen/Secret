package com.jayce.vexis.feedback

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.jayce.vexis.BaseActivity
import com.jayce.vexis.onlineUser
import com.jayce.vexis.databinding.ActivityFeedbackEditBinding
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.NetTool
import com.creezen.tool.NetTool.await
import kotlinx.coroutines.launch

class FeedbackEditActivity: BaseActivity() {

    private lateinit var binding: ActivityFeedbackEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        with(binding){
            submit.setOnClickListener {
                val titleMsg = title.msg()
                val contentMsg = content.msg()
                lifecycleScope.launch {
                    val result = NetTool
                        .create<FeedbackService>()
                        .sendFeedback(onlineUser.userId, titleMsg, contentMsg)
                        .await()
                    Log.e("FeedbackEditActivity.initView","$result")
                }
            }
            Log.e("FeedbackEditActivity.initView","click")
        }
    }
}