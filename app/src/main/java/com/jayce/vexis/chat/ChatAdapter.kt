package com.jayce.vexis.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jayce.vexis.databinding.ChatItemLayoutBinding

class ChatAdapter(private val msgList: List<ChatItem>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    class ViewHolder(val binding: ChatItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val nickname = binding.nickname
        val time = binding.time
        val msg = binding.msg
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ChatItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val items = msgList[position]
        holder.nickname.text = items.nickname
        holder.time.text = items.time
        holder.msg.text = items.msg
    }

    override fun getItemCount() = msgList.size

    override fun getItemViewType(position: Int) = position
}
