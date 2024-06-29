package com.ljw.secret.activities

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.ljw.secret.OnlineUserItem
import com.ljw.secret.bean.FeedbackBean
import com.ljw.secret.databinding.ActivityFeedbackEditBinding
import com.ljw.secret.network.FeedbackService
import com.ljw.secret.util.DataUtil.msg
import com.ljw.secret.util.NetworkServerCreator
import com.ljw.secret.util.await
import kotlinx.coroutines.launch

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
            submit.setOnClickListener {
                val titleMsg = title.msg()
                val contentMsg = content.msg()
                lifecycleScope.launch {
                    val result = NetworkServerCreator
                        .create<FeedbackService>()
                        .sendFeedback(OnlineUserItem.userId, titleMsg, contentMsg)
                        .await()
                    Log.e("FeedbackEditActivity.initView","$result")
                }
            }
            Log.e("FeedbackEditActivity.initView","click")
        }
    }
}