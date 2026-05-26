package com.jayce.vexis.business.mail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.creezen.commontool.Config.EVENT_TYPE_FEEDBACK
import com.jayce.vexis.databinding.CardItemLayoutBinding
import com.jayce.vexis.databinding.MailFeedbackItemBinding
import com.jayce.vexis.domain.bean.EventEntry
import com.jayce.vexis.foundation.ui.CardAdapter

class MailEventAdapter(
    private var eventList: List<EventEntry>
) : CardAdapter<EventEntry, ViewBinding, MailEventAdapter.ViewHolder>(eventList) {

    class ViewHolder(
        cardBinding: CardItemLayoutBinding,
        itemBinding: ViewBinding
    ) : CardAdapter.ViewHolder(cardBinding){
        val bind = itemBinding as MailFeedbackItemBinding
        val message = bind.message
        val user = bind.handleUser
    }

    override fun bindCardViewHolder(holder: ViewHolder, position: Int) {
        val item = eventList[position]
        val type = holder.itemViewType
        val message = if (type == EVENT_TYPE_FEEDBACK) {
            "您有新的反馈信息“${item.content}”"
        } else {
            item.content
        }
        val user = if (type == EVENT_TYPE_FEEDBACK) {
            "发起人： ${item.nickName}"
        } else {
            "操作人： ${item.nickName}"
        }
        holder.message.text = message
        holder.user.text = user
    }

    override fun getChildAndHoler(
        viewType: Int,
        layoutInflater: LayoutInflater,
        containerBinding: CardItemLayoutBinding,
        parent: ViewGroup
    ): Pair<ViewBinding, ViewHolder> {
        val child = MailFeedbackItemBinding.inflate(layoutInflater, parent, false)
        val holder = ViewHolder(containerBinding, child)
        return child to holder
    }

    override fun getAttachedList() = eventList

    override fun getItemCount() = eventList.size

    override fun updateAttachedList(newList: List<EventEntry>) { eventList = newList }

    override fun getItemViewType(position: Int) = eventList[position].type
}