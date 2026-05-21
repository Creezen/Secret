package com.jayce.vexis.business.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jayce.vexis.core.base.BaseAdapter
import com.jayce.vexis.databinding.ChatItemLayoutBinding
import com.jayce.vexis.domain.bean.ChatEntry

class ChatAdapter(private var msgList: ArrayList<ChatEntry>) : BaseAdapter<ChatEntry, ChatAdapter.ViewHolder>() {

    class ViewHolder(val binding: ChatItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val nickname = binding.nickname
        val time = binding.time
        val msg = binding.msg
        val view = binding.root
    }

    override fun getAttachedList() = msgList

    override fun updateAttachedList(newList: List<ChatEntry>) {
        msgList = ArrayList(newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChatItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatItem = msgList[position]
        holder.nickname.text = chatItem.nickname
        holder.time.text = chatItem.time
        holder.msg.text = chatItem.msg
    }

    override fun getItemCount() = msgList.size

    override fun getItemViewType(position: Int) = position
}