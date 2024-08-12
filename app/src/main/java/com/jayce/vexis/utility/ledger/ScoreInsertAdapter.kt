package com.jayce.vexis.utility.ledger

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creezen.tool.AndroidTool.intMsg
import com.jayce.vexis.databinding.AddRecordScoreBinding

class ScoreInsertAdapter(private val list: ArrayList<String>): RecyclerView.Adapter<ScoreInsertAdapter.ViewHolder>()  {

    private val scoreList = arrayListOf<Int>().apply {
        repeat(list.size) {
            add(0)
        }
        Log.e("ScoreInsertAdapter.","scoreList.size:  ${this.size}")
    }

    class ViewHolder(val binding: AddRecordScoreBinding): RecyclerView.ViewHolder(binding.root) {
        val userName = binding.userName
        val add = binding.add
        val sub = binding.sub
        val edit = binding.edit
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AddRecordScoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    fun getscoreList() = scoreList

    fun notifyUserAdded() {
        scoreList.add(0)
    }

    override fun getItemCount() = list.size

    override fun getItemViewType(position: Int) = position
}