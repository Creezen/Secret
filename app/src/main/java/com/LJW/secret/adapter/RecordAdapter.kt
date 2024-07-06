package com.ljw.secret.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ljw.secret.activities.pocket.NewRecord.Companion.HEIGHT
import com.ljw.secret.activities.pocket.NewRecord.Companion.WIDTH
import com.ljw.secret.bean.RecordItem
import com.ljw.secret.databinding.RecordItemLayoutBinding
import com.ljw.secret.util.AndroidUtil.addSimpleView

class RecordAdapter(private val recordList: List<RecordItem>): RecyclerView.Adapter<RecordAdapter.ViewHolder>()  {

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