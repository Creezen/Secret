package com.jayce.vexis.business.feedback

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.creezen.commontool.Config.AVATAR_SAVE_TIME
import com.creezen.commontool.bean.FeedbackBean
import com.creezen.commontool.toTime
import com.creezen.tool.AndroidTool.getData
import com.creezen.tool.AndroidTool.putData
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.TLog
import com.creezen.tool.ThreadTool
import com.creezen.tool.ThreadTool.runOnIO
import com.creezen.tool.ThreadTool.ui
import com.jayce.vexis.StatusManager.liveUser
import com.jayce.vexis.databinding.CardItemLayoutBinding
import com.jayce.vexis.databinding.FeedbackItemBinding
import com.jayce.vexis.domain.route.FeedbackService
import com.jayce.vexis.foundation.Util.Extension.load
import com.jayce.vexis.foundation.Util.Extension.onFalse
import com.jayce.vexis.foundation.Util.Extension.onTrue
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.ui.CardAdapter
import java.util.concurrent.ConcurrentHashMap

class FeedBackAdapter(private var feedbackList: List<FeedbackBean>) :
    CardAdapter<FeedbackBean, FeedbackItemBinding, FeedBackAdapter.ViewHolder>(feedbackList) {

    private val avatarMap: ConcurrentHashMap<Int, Long> = ConcurrentHashMap()

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

    override fun getItemCount() = feedbackList.size

    override fun getAttachedList() = feedbackList

    override fun updateAttachedList(newList: List<FeedbackBean>) {
        feedbackList = newList
    }

    override fun bindCardViewHolder(holder: ViewHolder, position: Int) {
        val item = feedbackList[position]
        holder.nickname.text = item.userName
        holder.time.text = item.createTime.toTime()
        holder.title.text = item.title
        holder.content.text = item.content
        holder.supportCount.text = "${item.support}"
        holder.support.setOnClickListener {
            TLog.d("click item: ${item.feedbackID}")
            request<FeedbackService, _>({ supportFeedback(item.feedbackID) }) {
                it.onTrue {
                    "点赞成功".toast()
                }.onFalse {
                    "点赞失败".toast()
                }
            }
        }

        runOnIO {
            val oldTime = avatarMap[position]
            val nowTime = getData(AVATAR_SAVE_TIME, 0L)
            if (oldTime == nowTime) return@runOnIO
            avatarMap[position] = nowTime
            ui {
                val url = "${item.userID}.png"
                holder.head.load(url, placeHolder = null, nowTime.toString(), true)
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