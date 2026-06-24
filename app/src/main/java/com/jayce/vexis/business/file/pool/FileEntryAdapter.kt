package com.jayce.vexis.business.file.pool

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jayce.vexis.util.bean.FileBean
import com.jayce.vexis.client.AndroidTool.toast
import com.jayce.vexis.client.FileTool.isFileDownload
import com.jayce.vexis.R
import com.jayce.vexis.core.base.BaseAdapter
import com.jayce.vexis.databinding.ResItemBinding
import com.jayce.vexis.domain.bean.DownloadTask
import com.jayce.vexis.domain.viewmodel.FileViewModel
import com.jayce.vexis.foundation.Util.Extension.parcelable

class FileEntryAdapter(
    private val context: Context,
    var list: List<FileBean>,
    private val viewModel: FileViewModel
) : BaseAdapter<FileBean, FileEntryAdapter.ViewHolder>() {

    class ViewHolder(var binding: ResItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val view = binding.root
        val name = binding.name
        val size = binding.size
        val time = binding.uploadTime
        val download = binding.download
    }

    override fun getAttachedList() = list

    override fun updateAttachedList(newList: List<FileBean>) { list = newList }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ResItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.view.isSelected = true
        holder.name.text = item.fileName
        holder.size.text = handleSizeDisplay(item.fileSize)
        holder.time.text = item.uploadTime
        val isDownload = isFileDownload("${item.fileID}${item.fileSuffix}", item.fileSize)
        if (isDownload) {
            holder.download.setImageResource(R.drawable.open)
        }
        holder.download.setOnClickListener {
            if (isFileDownload("${item.fileID}${item.fileSuffix}", item.fileSize)) {
                context.getString(R.string.file_exist_open).toast()
                return@setOnClickListener
            }
            val task = DownloadTask(
                item.fileID,
                "${item.fileID}${item.fileSuffix}",
                item.fileSize,
                System.currentTimeMillis(),
                1
            ) { holder.download.setImageResource(R.drawable.open) }
            viewModel.dispatchDownloadTask(task)
        }
        holder.view.setOnClickListener {
            context.startActivity(
                Intent(context, FileInformationActivity::class.java).apply {
                    putExtra("fileInfo", item.parcelable())
                },
            )
        }
    }

    override fun getItemCount() = list.size

    private fun handleSizeDisplay(size: Long): String {
        if (size < 1024) {
            return "$size b"
        }
        if (size < 1024 * 1024) {
            val sizeNum = size / 1024.0
            val finalNum = "%.2f".format(sizeNum)
            return "$finalNum kb"
        }
        val sizeNum = size / (1024.0 * 1024)
        val finalNum = "%.2f".format(sizeNum)
        return "$finalNum M"
    }
}