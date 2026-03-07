package com.jayce.vexis.business.feedback

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.creezen.commontool.Config.PreferenceParam.AVATAR_SAVE_TIME
import com.creezen.commontool.bean.FeedbackBean
import com.creezen.commontool.toTime
import com.creezen.tool.AndroidTool
import com.creezen.tool.NetTool
import com.creezen.tool.ThreadTool
import com.jayce.vexis.business.role.dashboard.AvatarSignnature
import com.jayce.vexis.core.SessionManager.BASE_FILE_PATH
import com.jayce.vexis.core.SessionManager.user
import com.jayce.vexis.databinding.CardItemLayoutBinding
import com.jayce.vexis.databinding.FeedbackItemBinding
import com.jayce.vexis.foundation.view.CardAdapter

class FeedBackAdapter(
    private val context: Context,
    private val feedbackEntryList: List<FeedbackBean>
) : CardAdapter<FeedbackItemBinding, FeedBackAdapter.ViewHolder>(feedbackEntryList) {

    class ViewHolder(
        containerBinding: CardItemLayoutBinding,
        binding: FeedbackItemBinding
    ) : CardAdapter.ViewHolder(containerBinding) {
        val view = binding.root
        val head = binding.head
        val nickname = binding.nickname
        val time = binding.time
        val title = binding.title
        val content = binding.content
        val support = binding.support
        val supportCount = binding.supportCount
        val against = binding.against
    }

    override fun bindCardViewHolder(holder: ViewHolder, position: Int) {
        val item = feedbackEntryList[position]
        holder.nickname.text = item.userName
        holder.time.text = item.createTime.toTime()
        holder.title.text = item.title
        holder.content.text = item.content
        holder.supportCount.text = "${item.support}"

        AndroidTool.getDataAsync(AVATAR_SAVE_TIME, 0L) {
            ThreadTool.ui {
                NetTool.setImage(
                    context,
                    holder.head,
                    "${BASE_FILE_PATH}head/${user().userId}.png",
                    key = AvatarSignnature("key:$it"),
                    isCircle = true
                )
            }

        }
    }

    override fun getChildAndHoler(
        layoutInflater: LayoutInflater,
        containerBinding: CardItemLayoutBinding,
        parent: ViewGroup
    ): Pair<FeedbackItemBinding, ViewHolder> {
        val childBinding = FeedbackItemBinding.inflate(layoutInflater, parent,false)
        val holder = ViewHolder(containerBinding, childBinding)
        return childBinding to holder
    }
}