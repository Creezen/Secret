package com.jayce.vexis.business.role.manage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jayce.vexis.util.bean.ActiveBean
import com.jayce.vexis.client.AndroidTool.toast
import com.jayce.vexis.databinding.ActionWindowBinding
import com.jayce.vexis.databinding.CardItemLayoutBinding
import com.jayce.vexis.databinding.UserActiveItemBinding
import com.jayce.vexis.foundation.Util.Extension.parcelable
import com.jayce.vexis.foundation.ui.CardAdapter
import com.jayce.vexis.foundation.ui.block.ActionMenu

class UserActiveAdapter(
    private val context: Context,
    private var userList: List<ActiveBean>,
) : CardAdapter<ActiveBean, UserActiveItemBinding, UserActiveAdapter.ViewHolder>(userList) {

    class ViewHolder(
        containerBindig: CardItemLayoutBinding,
        binding: UserActiveItemBinding
    ) : CardAdapter.ViewHolder(containerBindig) {
        val view = binding.root
        val nickname = binding.nickname
        val admin = binding.administrator
        val id = binding.userID
        val time = binding.createTime
    }

    override fun getItemCount() = userList.size

    override fun getAttachedList() = userList

    override fun updateAttachedList(newList: List<ActiveBean>) {
        userList = newList
    }

    override fun bindCardViewHolder(holder: ViewHolder, position: Int) {
        val item = userList[position]
        val window = bindPopupWindow(holder.view, item)
        holder.nickname.userName = item.nickname ?: "未知用户"
        holder.nickname.isAdmin = item.isAdministrator()
        holder.admin.visibility = if (item.isAdministrator()) View.VISIBLE else View.GONE
        holder.id.text = item.userID
        holder.time.text = item.createTime
        holder.view.setOnClickListener {
            val intent = Intent(context, ActiveDataActivity::class.java)
            intent.putExtra("activeItem", item.parcelable())
            context.startActivity(intent)
        }
        holder.view.setOnLongClickListener {
            window.show()
            true
        }
    }

    override fun getChildAndHoler(
        viewType: Int,
        layoutInflater: LayoutInflater,
        containerBinding: CardItemLayoutBinding,
        parent: ViewGroup
    ): Pair<UserActiveItemBinding, ViewHolder> {
        val childBinding = UserActiveItemBinding.inflate(layoutInflater, parent, false)
        val holder = ViewHolder(containerBinding, childBinding)
        return childBinding to holder
    }

    private fun bindPopupWindow(view: View, activeItem: ActiveBean): ActionMenu {
        val bind = ActionWindowBinding.inflate((context as Activity).layoutInflater)
        val window = ActionMenu(bind.root, view)
        bind.setAdmin.setOnClickListener {
            "${activeItem.nickname}已成为管理员".toast()
            window.dismiss()
        }
        bind.deleteUser.setOnClickListener {
            "删除${activeItem.nickname}".toast()
            window.dismiss()
        }
        return window
    }
}