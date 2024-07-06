package com.ljw.secret.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ljw.secret.databinding.AddRecordScoreBinding
import com.ljw.secret.util.DataUtil.intMsg

class AddScoreAdapter(private val list: ArrayList<String>): RecyclerView.Adapter<AddScoreAdapter.ViewHolder>()  {

    private val scoreList = arrayListOf<Int>().apply {
        repeat(list.size) {
            add(0)
        }
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