package com.jayce.vexis.business.file.module

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jayce.vexis.client.AndroidTool.toast
import com.jayce.vexis.core.base.BaseAdapter
import com.jayce.vexis.databinding.DynamicDialogItemBinding
import com.jayce.vexis.foundation.ui.block.RingProgress
import com.jayce.vexis.util.vo.DynamicItemVO
import com.jayce.vexis.util.vo.DynamicVO

class DynamicDialogAdapter(
    private val context: Context,
    private var list: List<DynamicItemVO>,
    private val onClick: (DynamicItemVO, RingProgress) -> Unit
) : BaseAdapter<DynamicItemVO, DynamicDialogAdapter.ViewHolder>() {

    override fun getAttachedList() = list

    override fun getItemCount() = list.size

    override fun updateAttachedList(newList: List<DynamicItemVO>) {
        list = newList
    }

    class ViewHolder(val binding: DynamicDialogItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.name
        val size = binding.size
        val download = binding.download
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bind = DynamicDialogItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(bind)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.name.text = item.name
        holder.size.text = item.size.toString()
        holder.download.strokePercent = 0.05f
        onClick.invoke(item, holder.download)
    }
}