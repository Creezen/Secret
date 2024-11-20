package com.jayce.vexis.utility.ledger.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creezen.tool.AndroidTool.addSimpleView
import com.jayce.vexis.databinding.RecordItemLayoutBinding
import com.jayce.vexis.utility.ledger.bean.RecordBean
import com.jayce.vexis.utility.ledger.ScoreBoard.Companion.HEIGHT
import com.jayce.vexis.utility.ledger.ScoreBoard.Companion.WIDTH

class RecordAdapter(private val recordList: List<RecordBean>): RecyclerView.Adapter<RecordAdapter.ViewHolder>() {

    class ViewHolder(val binding: RecordItemLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        val view = binding.itemLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecordItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val items = recordList[position]
        holder.view.removeAllViews()
        items.scores.forEach {
            holder.view.addSimpleView("$it", WIDTH, HEIGHT)
        }
    }

    override fun getItemCount() = recordList.size

    override fun getItemViewType(position: Int) = position
}