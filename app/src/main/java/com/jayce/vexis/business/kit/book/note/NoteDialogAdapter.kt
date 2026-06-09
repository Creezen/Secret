package com.jayce.vexis.business.kit.book.note

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creezen.tool.AndroidTool.intMsg
import com.creezen.tool.TLog
import com.jayce.vexis.core.base.BaseAdapter
import com.jayce.vexis.databinding.BookDialogLineNoteBinding

class NoteDialogAdapter(private var list: List<String>) : BaseAdapter<String, NoteDialogAdapter.ViewHolder>() {

    private val scoreList = arrayListOf<Int>().apply {
        repeat(list.size) { add(0) }
        TLog.e("scoreList.size:  ${this.size}")
    }

    override fun getAttachedList() = list

    override fun updateAttachedList(newList: List<String>) {
        list = newList
    }

    class ViewHolder(val binding: BookDialogLineNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        val userName = binding.userName
        val add = binding.add
        val sub = binding.sub
        val edit = binding.edit
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BookDialogLineNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.apply {
            edit.setText("0")
            userName.text = item
            add.setOnClickListener {
                val intMsg = edit.intMsg() + 1
                edit.setText("$intMsg")
                scoreList[position] = intMsg
            }
            sub.setOnClickListener {
                val intMsg = edit.intMsg() - 1
                edit.setText("$intMsg")
                scoreList[position] = intMsg
            }
        }
    }

    fun getLineList() = scoreList

    fun notifyUserAdded() { scoreList.add(0) }

    override fun getItemCount() = list.size

    override fun getItemViewType(position: Int) = position
}