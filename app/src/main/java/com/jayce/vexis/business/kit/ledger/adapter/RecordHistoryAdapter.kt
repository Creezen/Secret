package com.jayce.vexis.business.kit.ledger.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.jayce.vexis.databinding.RecordListLayoutBinding
import com.jayce.vexis.business.kit.ledger.ScoreBoard
import com.jayce.vexis.business.kit.ledger.bean.RecordListItem
import com.jayce.vexis.business.kit.ledger.database.ScoreDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecordHistoryAdapter(
    private val context: Context,
    private val list: List<RecordListItem>,
    private val owner: LifecycleOwner,
) : RecyclerView.Adapter<RecordHistoryAdapter.ViewHolder>() {
    companion object {
        const val TAG = "RecordHistoryAdapter"
    }

    private val scoreDao by lazy {
        ScoreDatabase.getDatabase(context).recordDao()
    }

    class ViewHolder(val binding: RecordListLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val view = binding.root
        val title = binding.title
        val time = binding.time
        val result = binding.result
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = RecordListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
            owner.lifecycleScope.launch(Dispatchers.IO) {
                val scoreList = scoreDao.getScoreList(items.id)
                val userList = scoreList.userList.split("$")
                Log.e(TAG, "$userList")
                val intent =
                    Intent(context, ScoreBoard::class.java).also {
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
