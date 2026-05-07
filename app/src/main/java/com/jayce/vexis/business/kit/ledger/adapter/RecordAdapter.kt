package com.jayce.vexis.business.kit.ledger.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creezen.tool.AndroidTool.addSimpleView
import com.jayce.vexis.databinding.RecordItemLayoutBinding
import com.jayce.vexis.domain.bean.RecordEntry
import com.jayce.vexis.business.kit.ledger.ScoreBoardActivity.Companion.HEIGHT
import com.jayce.vexis.business.kit.ledger.ScoreBoardActivity.Companion.WIDTH
import com.jayce.vexis.core.base.BaseAdapter

class RecordAdapter(private var recordList: List<RecordEntry>) : BaseAdapter<RecordEntry, RecordAdapter.ViewHolder>() {

    class ViewHolder(val binding: RecordItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val view = binding.itemLayout
    }

    override fun getAttachedList() = recordList

    override fun updateAttachedList(newList: List<RecordEntry>) {
        recordList = newList
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = RecordItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val items = recordList[position]
        holder.view.removeAllViews()
        items.scores.forEach {
            holder.view.addSimpleView("$it", WIDTH, HEIGHT)
        }
    }

    override fun getItemCount() = recordList.size

    override fun getItemViewType(position: Int) = position
}