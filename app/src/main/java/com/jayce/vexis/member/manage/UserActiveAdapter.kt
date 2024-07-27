package com.jayce.vexis.member.manage

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jayce.vexis.member.ActiveItem
import com.jayce.vexis.databinding.UserActiveItemBinding

class UserActiveAdapter(private val context: Context, private val userList: List<ActiveItem>): RecyclerView.Adapter<UserActiveAdapter.ViewHolder>()  {

    class ViewHolder(val binding: UserActiveItemBinding): RecyclerView.ViewHolder(binding.root) {
        val view = binding.root
        val nickname = binding.nickname
        val admin = binding.administrator
        val id = binding.userID
        val time = binding.createTime
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = UserActiveItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = userList[position]
        holder.nickname.text = item.nickname
        holder.admin.visibility = if (item.administrator == 0) View.GONE else View.VISIBLE
        holder.id.text = item.userID
        holder.time.text = item.createTime
        holder.view.setOnClickListener {
            context.startActivity(Intent(context, ActiveDataActivity::class.java).also {
                it.putExtra("activeItem", item)
            })
        }
    }

    override fun getItemCount() = userList.size

    override fun getItemViewType(position: Int) = position
}