package com.jayce.vexis.business.kit.book.archive

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jayce.vexis.business.kit.book.note.LineNoteActivity
import com.jayce.vexis.client.ThreadTool
import com.jayce.vexis.core.base.BaseAdapter
import com.jayce.vexis.databinding.BookArchiveBinding
import com.jayce.vexis.domain.bean.book.BookArchiveEntry
import com.jayce.vexis.domain.database.book.BookDatabase

class ArchiveAdapter(
    private val context: Context,
    private var list: List<BookArchiveEntry>,
) : BaseAdapter<BookArchiveEntry, ArchiveAdapter.ViewHolder>() {

    private val scoreDao by lazy {
        BookDatabase.getDatabase(context).recordDao()
    }

    override fun getAttachedList() = list

    override fun updateAttachedList(newList: List<BookArchiveEntry>) {
        list = newList
    }

    class ViewHolder(val binding: BookArchiveBinding) : RecyclerView.ViewHolder(binding.root) {
        val view = binding.root
        val title = binding.title
        val time = binding.time
        val result = binding.result
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BookArchiveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val items = list[position]
        holder.title.text = items.title
        holder.time.text = items.time
        holder.result.text = items.result
        holder.view.setOnClickListener {
            ThreadTool.runOnMulti {
                val scoreList = scoreDao.getScoreList(items.id)
                val userList = scoreList.userList.split("$")
                val intent = Intent(context, LineNoteActivity::class.java).also {
                    it.putExtra("userData", arrayListOf<String>().also { it.addAll(userList) })
                }
                context.startActivity(intent)
//                Log.e(TAG,"${scoreList.scoreList} ${scoreList.userList}  " +
//                        "${scoreList.totalStr}  ")
            }
        }
    }

    override fun getItemCount() = list.size

    override fun getItemViewType(position: Int) = position
}