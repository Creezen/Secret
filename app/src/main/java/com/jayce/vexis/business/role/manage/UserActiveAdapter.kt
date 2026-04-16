package com.jayce.vexis.business.role.manage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.PopupWindow
import com.creezen.commontool.bean.ActiveBean
import com.creezen.tool.AndroidTool.toast
import com.jayce.vexis.R
import com.jayce.vexis.databinding.CardItemLayoutBinding
import com.jayce.vexis.databinding.PoupWindowBinding
import com.jayce.vexis.databinding.UserActiveItemBinding
import com.jayce.vexis.foundation.Util.Extension.parcelable
import com.jayce.vexis.foundation.ui.block.TrackPopupMenu
import com.jayce.vexis.foundation.view.CardAdapter

class UserActiveAdapter(
    private val context: Context,
    private val userList: List<ActiveBean>,
) : CardAdapter<UserActiveItemBinding, UserActiveAdapter.ViewHolder>(userList) {

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

    override fun bindCardViewHolder(holder: ViewHolder, position: Int) {
        val item = userList[position]
        val window = initPopupWindow(holder.view, item)
        holder.nickname.text = item.nickname
        holder.admin.visibility = if (item.administrator == 0) View.GONE else View.VISIBLE
        holder.id.text = item.userID
        holder.time.text = item.createTime
        holder.view.setOnClickListener {
            context.startActivity(
                Intent(context, ActiveDataActivity::class.java).also {
                    it.putExtra("activeItem", item.parcelable())
                },
            )
        }
        holder.view.setOnLongClickListener {
            window.show()
            true
        }
    }

    override fun getChildAndHoler(
        layoutInflater: LayoutInflater,
        containerBinding: CardItemLayoutBinding,
        parent: ViewGroup
    ): Pair<UserActiveItemBinding, ViewHolder> {
        val childBinding = UserActiveItemBinding.inflate(layoutInflater, parent,false)
        val holder = ViewHolder(containerBinding, childBinding)
        return childBinding to holder
    }

    private fun initPopupWindow(view: View, activeItem: ActiveBean): TrackPopupMenu {
        val bind = PoupWindowBinding.inflate((context as Activity).layoutInflater)
        val window = TrackPopupMenu(bind.root, view)
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