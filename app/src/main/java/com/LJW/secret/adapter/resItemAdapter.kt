package com.ljw.secret.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.ljw.secret.R
import com.ljw.secret.activities.FileDetailActivity
import com.ljw.secret.bean.ResourceItem
import com.ljw.secret.databinding.ResItemBinding
import com.ljw.secret.network.FileService
import com.ljw.secret.util.DataUtil.toast
import com.ljw.secret.util.FileUtil.downloadFileByNet
import com.ljw.secret.util.FileUtil.isFileDownload
import com.ljw.secret.util.NetworkServerCreator
import com.ljw.secret.util.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class ResItemAdapter(private val context: Context, private val parent: LifecycleOwner, val list: List<ResourceItem>): RecyclerView.Adapter<ResItemAdapter.ViewHodler>() {

    private var progressBar: ProgressBar? = null

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
        var isDownload = isFileDownload(item.fileName, item.fileSize)
        if (isDownload) {
            holder.download.setImageResource(R.drawable.open)
        }
        holder.download.setOnClickListener {
            if (isDownload) {
                "文件已存在，是否打开?".toast()
                return@setOnClickListener
            }
            "开始下载".toast()
            progressBar?.progress = 0
            parent.lifecycleScope.launch(Dispatchers.IO) {
                val fileName = "${item.fileID}${item.fileSuffix}"
                val responseStream = NetworkServerCreator
                    .create<FileService>()
                    .downloadFile(fileName)
                    .await()
                    .byteStream()
                progressBar?.max = item.fileSize.toInt()
                downloadFileByNet(responseStream, item.fileName) { size ->
                    progressBar?.progress = size
                }
            }
        }
        holder.view.setOnClickListener {
            context.startActivity(Intent(context, FileDetailActivity::class.java).also {
                it.putExtra("fileInfo", item)
            })
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
        return "$finalNum M"
    }

    fun setProgressBar(bar: ProgressBar) {
        this.progressBar = bar
    }
}