package com.jayce.vexis.business.peer

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creezen.commontool.bean.PeerAdviceBean
import com.creezen.tool.AndroidTool.toast
import com.jayce.vexis.R
import com.jayce.vexis.databinding.SeniorAdviceBinding

class PeerAdapter(
    val context: Context,
    val adviceList: List<PeerAdviceBean>,
) : RecyclerView.Adapter<PeerAdapter.ViewHolder>() {

    class ViewHolder(val binding: SeniorAdviceBinding) : RecyclerView.ViewHolder(binding.root) {
        val content = binding.adviceContent
        val love = binding.love
        val resource = binding.resource
        val reply = binding.reply
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = SeniorAdviceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val item = adviceList[position]
        holder.content.text = item.content
        holder.reply.setOnClickListener {
            "点我回复".toast()
        }
        if (position % 2 == 0) {
            holder.resource.setImageResource(R.drawable.file_resource)
            holder.resource.setOnClickListener {
                "点我获取资源".toast()
            }

            holder.love.setImageResource(R.drawable.support_blank_clicked)
        } else {
            holder.resource.isClickable = false
            holder.resource.setImageResource(R.drawable.file_resource_unable)

            holder.love.setImageResource(R.drawable.support_blank)
        }
        holder.love.setOnClickListener {
            "谢谢你的喜欢".toast()
        }
    }

    override fun getItemCount() = adviceList.size
}