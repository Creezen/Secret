package com.ljw.secret.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ljw.secret.bean.ResourceItem
import com.ljw.secret.databinding.ResItemBinding
import com.ljw.secret.util.DataUtil.toast

class ResItemAdapter(val list: List<ResourceItem>): RecyclerView.Adapter<ResItemAdapter.ViewHodler>() {

    class ViewHodler(val binding: ResItemBinding): RecyclerView.ViewHolder(binding.root){
        val view = binding.root
        val name = binding.name
        val size = binding.size
        val time = binding.uploadTime
        val download = binding.download
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHodler {
        val binding = ResItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHodler(binding)
    }

    override fun onBindViewHolder(holder: ViewHodler, position: Int) {
        val item = list[position]
        holder.view.isSelected = true
        holder.name.text = item.fileName
        holder.size.text = handleSizeDisplay(item.fileSize)
        holder.time.text = item.uploadTime
        holder.download.setOnClickListener {
            item.description.toast()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun handleSizeDisplay(size: Long): String {
        if (size < 1024) {
            return "$size b"
        }
        if (size < 1024 * 1024) {
            val sizeNum = size/1024.0
            val finalNum = "%.2f".format(sizeNum)
            return "$finalNum kb"
        }
        val sizeNum = size/(1024.0 * 1024)
        val finalNum = "%.2f".format(sizeNum)
        return "$finalNum mb"
    }
}