package com.jayce.vexis.foundation.ui.block.mention

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jayce.vexis.core.base.BaseAdapter
import com.jayce.vexis.databinding.MentionDialogUserItemBinding
import com.jayce.vexis.util.vo.UserVO

class MentionSearchAdapter(
    private val context: Context,
    private var list: List<UserVO>
) : BaseAdapter<UserVO, MentionSearchAdapter.ViewHolder>() {

    private var onItemClick: ((UserVO) -> Unit)? = null

    override fun getAttachedList() = list

    override fun getItemCount() = list.size

    override fun updateAttachedList(newList: List<UserVO>) {
        list = newList
    }

    class ViewHolder(
        val bind: MentionDialogUserItemBinding
    ) : RecyclerView.ViewHolder(bind.root) {
        val view = bind.root
        val userName = bind.userName
        val userId = bind.userId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MentionDialogUserItemBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.userName.text = item.profile.nickname
        holder.userId.text = item.userId
        holder.view.setOnClickListener {
            onItemClick?.invoke(item)
        }
    }

    fun setOnItemClick(onClick: (UserVO) -> Unit) {
        onItemClick = onClick
    }
}