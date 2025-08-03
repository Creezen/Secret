package com.jayce.vexis.business.feedback

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creezen.commontool.CreezenTool.toTime
import com.creezen.tool.AndroidTool
import com.creezen.tool.NetTool
import com.jayce.vexis.core.Config.BASE_FILE_PATH
import com.jayce.vexis.core.SessionManager.user
import com.jayce.vexis.databinding.FeedbackItemBinding
import com.jayce.vexis.business.member.dashboard.AvatarSignnature
import com.jayce.vexis.foundation.bean.FeedbackEntry

class FeedbackAdapter(
    val context: Context,
    val feedbackEntryList: List<FeedbackEntry>,
) : RecyclerView.Adapter<FeedbackAdapter.ViewHolder>() {
    class ViewHolder(val binding: FeedbackItemBinding) : RecyclerView.ViewHolder(binding.root) {
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = FeedbackItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val item = feedbackEntryList[position]
        holder.nickname.text = item.userName
        holder.time.text = item.createTime.toTime()
        holder.title.text = item.title
        holder.content.text = item.content
        holder.supportCount.text = "${item.support}"

        val avatarTimestamp = AndroidTool.readPrefs {
            it.getLong("cursorTime", 0)
        }
        NetTool.setImage(
            context,
            holder.head,
            "${BASE_FILE_PATH}head/${user().userId}.png",
            key = AvatarSignnature("key:$avatarTimestamp"),
            isCircle = true
        )
    }

    override fun getItemCount() = feedbackEntryList.size
}