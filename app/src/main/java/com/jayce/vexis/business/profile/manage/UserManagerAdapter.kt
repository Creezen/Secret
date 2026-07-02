package com.jayce.vexis.business.profile.manage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jayce.vexis.R
import com.jayce.vexis.databinding.CardItemLayoutBinding
import com.jayce.vexis.databinding.UserActiveItemBinding
import com.jayce.vexis.domain.route.UserService
import com.jayce.vexis.foundation.Util.Extension.parcelable
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.ability.menu.MenuManager.registerOnMenuClick
import com.jayce.vexis.foundation.ability.menu.OnMenuClick
import com.jayce.vexis.foundation.ui.CardAdapter
import com.jayce.vexis.util.bean.ActiveBean

class UserManagerAdapter(
    private val context: Context,
    private var userList: List<ActiveBean>,
) : CardAdapter<ActiveBean, UserActiveItemBinding, UserManagerAdapter.ViewHolder>(userList), OnMenuClick {

    private var onManagerSuccess: ((Int) -> Unit)? = null

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
        holder.nickname.userName = item.nickname ?: "未知用户"
        holder.nickname.isAdmin = item.isAdministrator()
        holder.admin.visibility = if (item.isAdministrator()) View.VISIBLE else View.GONE
        holder.id.text = item.userID
        holder.time.text = item.createTime
        holder.view.setOnClickListener {
            val intent = Intent(context, UserManagerActivity::class.java)
            intent.putExtra("activeItem", item.parcelable())
            context.startActivity(intent)
        }
        val bundle = Bundle().apply { putString("id", item.userID) }
        holder.view.registerOnMenuClick(context, R.menu.user_manager_action, bundle, this)
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

    fun setOnManagerCallback(callBack: (Int) -> Unit) {
        this.onManagerSuccess = callBack
    }

    override fun onMenuItemClicked(menuId: Int, bundle: Bundle?) {
        val id = bundle?.getString("id") ?: return
        when (menuId) {
            R.id.userManagerAdmin -> {
                request<UserService, _>({ setUserAsAdmin(id) }) {
                    if (!it) return@request
                    this.onManagerSuccess?.invoke(0)
                }
            }
            R.id.userManagerDelete -> {
                request<UserService, _>({ deleteUser(id) }) {
                    if (!it) return@request
                    this.onManagerSuccess?.invoke(1)
                }
            }
        }
    }
}