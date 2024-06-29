package com.ljw.secret.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ljw.secret.bean.FeedbackItem
import com.ljw.secret.databinding.FeedbackItemBinding
import com.ljw.secret.util.DataUtil.toTime

class FeedbackAdapter(val feedbackItemList: List<FeedbackItem>): RecyclerView.Adapter<FeedbackAdapter.ViewHolder>() {

    class ViewHolder(val binding: FeedbackItemBinding): RecyclerView.ViewHolder(binding.root) {
        val view = binding.root
        val head = binding.head
        val nickname = binding.nickname
        val time = binding.time
        val title = binding.title
        val content = binding.content
        val support = binding.support
        val supportCount = binding.supportCount
        val against = binding.against
        val againstCount = binding.againstCount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FeedbackItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = feedbackItemList[position]
        holder.nickname.text = item.userID
        holder.time.text = item.createTime.toTime()
        holder.title.text = item.title
        holder.content.text = item.content
        holder.supportCount.text = "${item.support}"
        holder.againstCount.text = "${item.against}"
    }

    override fun getItemCount() = feedbackItemList.size
}