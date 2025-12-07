package com.jayce.vexis.business.file.pool

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.creezen.commontool.bean.FileBean
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.FileTool.downloadFileByNet
import com.creezen.tool.FileTool.isFileDownload
import com.jayce.vexis.R
import com.jayce.vexis.databinding.ResItemBinding
import com.jayce.vexis.foundation.Util.Extension.parcelable
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.route.FileService
import okhttp3.ResponseBody

class FileEntryAdapter(
    private val context: Context,
    val list: List<FileBean>,
) : RecyclerView.Adapter<FileEntryAdapter.ViewHodler>() {
    companion object {
        const val TAG = "MediaElementAdapter"
    }

    private var progressBar: ProgressBar? = null

    class ViewHodler(val binding: ResItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val view = binding.root
        val name = binding.name
        val size = binding.size
        val time = binding.uploadTime
        val download = binding.download
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHodler {
        val binding = ResItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHodler(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHodler,
        position: Int,
    ) {
        val item = list[position]
        holder.view.isSelected = true
        holder.name.text = item.fileName
        holder.size.text = handleSizeDisplay(item.fileSize)
        holder.time.text = item.uploadTime
        val isDownload = isFileDownload(item.fileName, item.fileSize)
        if (isDownload) {
            holder.download.setImageResource(R.drawable.open)
        }
        holder.download.setOnClickListener {
            if (isDownload) {
                context.getString(R.string.file_exist_open).toast()
                return@setOnClickListener
            }
            context.getString(R.string.begin_download).toast()
            progressBar?.progress = 0
            val fileName = "${item.fileID}${item.fileSuffix}"
            request<FileService, ResponseBody>({ downloadFile(fileName) }) {
                val stream = it.byteStream()
                progressBar?.max = item.fileSize.toInt()
                downloadFileByNet(stream, item.fileName) { size ->
                    progressBar?.progress = size
                    if (size == item.fileSize.toInt()) {
                        holder.download.setImageResource(R.drawable.open)
                    }
                }
            }
        }
        holder.view.setOnClickListener {
            context.startActivity(
                Intent(context, FileInfomationActivity::class.java).apply {
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

    fun setProgressBar(bar: ProgressBar) {
        this.progressBar = bar
    }
}