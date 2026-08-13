package com.jayce.vexis.business.feedback

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jayce.vexis.R
import com.jayce.vexis.StatusManager.liveUser
import com.jayce.vexis.business.profile.dashboard.DashboardActivity
import com.jayce.vexis.client.AndroidTool.getData
import com.jayce.vexis.client.AndroidTool.toast
import com.jayce.vexis.client.ThreadTool.runOnIO
import com.jayce.vexis.client.ThreadTool.ui
import com.jayce.vexis.client.bean.ImageOption
import com.jayce.vexis.databinding.CardItemLayoutBinding
import com.jayce.vexis.databinding.FeedbackItemBinding
import com.jayce.vexis.domain.route.FeedbackService
import com.jayce.vexis.foundation.Util.Extension.jumpTo
import com.jayce.vexis.foundation.Util.Extension.load
import com.jayce.vexis.foundation.Util.Extension.onFalse
import com.jayce.vexis.foundation.Util.Extension.onTrue
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.ui.CardAdapter
import com.jayce.vexis.util.Config.AVATAR_SAVE_TIME
import com.jayce.vexis.util.toTime
import com.jayce.vexis.util.vo.FeedbackVO

class FeedBackAdapter(
    private val context: Context,
    private var feedbackList: List<FeedbackVO>
): CardAdapter<FeedbackVO, FeedbackItemBinding, FeedBackAdapter.ViewHolder>(feedbackList) {

    class ViewHolder(
        containerBinding: CardItemLayoutBinding,
        binding: FeedbackItemBinding
    ) : CardAdapter.ViewHolder(containerBinding) {
        val view = binding.root
        val feedbackUser = binding.feedbackUser
        val head = binding.head
        val nickname = binding.nickname
        val time = binding.time
        val title = binding.title
        val content = binding.content
        val support = binding.support
        val supportCount = binding.supportCount
        val against = binding.against
    }

    override fun getItemCount() = feedbackList.size

    override fun getAttachedList() = feedbackList

    override fun updateAttachedList(newList: List<FeedbackVO>) {
        feedbackList = newList
    }

    override fun bindCardViewHolder(holder: ViewHolder, position: Int) {
        val item = feedbackList[position]
        holder.nickname.text = item.userName
        holder.time.text = item.createTime.toTime()
        holder.title.text = item.title
        holder.content.text = item.content
        holder.supportCount.text = "${item.support}"
        holder.feedbackUser.setOnClickListener {
            context.jumpTo(DashboardActivity::class.java) {
                putExtra("userId", item.userID)
            }
        }
        holder.support.setOnClickListener {
            request<FeedbackService, _>({ supportFeedback(liveUser.userId, item.feedbackID) }) {
                it.onTrue {
                    "点赞成功".toast()
                }.onFalse {
                    "点赞已取消".toast()
                }
            }
        }

        runOnIO {
            val nowTime = getData(AVATAR_SAVE_TIME, 0L)
            ui {
                val option = ImageOption(true, nowTime.toString(), "/head")
                if (item.userID.isEmpty()) {
                    holder.head.load(R.drawable.out_user, option)
                } else {
                    holder.head.load("${item.userID}.png", option)
                }
            }
        }
    }

    override fun getChildAndHoler(
        viewType: Int,
        layoutInflater: LayoutInflater,
        containerBinding: CardItemLayoutBinding,
        parent: ViewGroup
    ): Pair<FeedbackItemBinding, ViewHolder> {
        val childBinding = FeedbackItemBinding.inflate(layoutInflater, parent, false)
        val holder = ViewHolder(containerBinding, childBinding)
        return childBinding to holder
    }
}