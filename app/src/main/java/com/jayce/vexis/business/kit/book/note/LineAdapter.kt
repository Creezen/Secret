package com.jayce.vexis.business.kit.book.note

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jayce.vexis.client.AndroidTool.addSimpleView
import com.jayce.vexis.business.kit.book.note.LineNoteActivity.Companion.HEIGHT
import com.jayce.vexis.business.kit.book.note.LineNoteActivity.Companion.WIDTH
import com.jayce.vexis.core.base.BaseAdapter
import com.jayce.vexis.databinding.BookLineBinding
import com.jayce.vexis.domain.bean.LineEntry

class LineAdapter(private var recordList: List<LineEntry>) : BaseAdapter<LineEntry, LineAdapter.ViewHolder>() {

    class ViewHolder(val binding: BookLineBinding) : RecyclerView.ViewHolder(binding.root) {
        val view = binding.itemLayout
    }

    override fun getAttachedList() = recordList

    override fun updateAttachedList(newList: List<LineEntry>) {
        recordList = newList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BookLineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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