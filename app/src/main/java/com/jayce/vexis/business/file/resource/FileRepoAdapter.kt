package com.jayce.vexis.business.file.resource

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.jayce.vexis.R
import com.jayce.vexis.client.AndroidTool.fileExplore
import com.jayce.vexis.client.ThreadTool.runOnIO
import com.jayce.vexis.client.ThreadTool.ui
import com.jayce.vexis.core.base.BaseAdapter
import com.jayce.vexis.databinding.ResItemBinding
import com.jayce.vexis.domain.bo.DownloadTaskBO
import com.jayce.vexis.domain.database.file.FileDatabase
import com.jayce.vexis.domain.database.file.FileEntity
import com.jayce.vexis.domain.viewmodel.FileViewModel
import com.jayce.vexis.foundation.Util.Extension.entity
import com.jayce.vexis.foundation.Util.Extension.jumpTo
import com.jayce.vexis.util.vo.FileVO

class FileRepoAdapter(
    private val context: Context,
    var list: List<FileVO>,
    private val viewModel: FileViewModel
) : BaseAdapter<FileVO, FileRepoAdapter.ViewHolder>() {

    private val fileDao by lazy { FileDatabase.getDatabse(context).fileDao() }

    class ViewHolder(var binding: ResItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val view = binding.root
        val name = binding.name
        val size = binding.size
        val time = binding.uploadTime
        val download = binding.download
    }

    override fun getAttachedList() = list

    override fun updateAttachedList(newList: List<FileVO>) { list = newList }

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
        var cacheItem: FileEntity? = null
        runOnIO {
            cacheItem = fileDao.queryItemByHash(item.fileHash)
            if (cacheItem != null) {
                ui { updateImage(cacheItem, holder.download) }
            }
        }

        holder.download.setOnClickListener {
            runOnIO {
                cacheItem = fileDao.queryItemByHash(item.fileHash)
                if (cacheItem != null) {
                    ui{ explore(cacheItem) }
                    return@runOnIO
                }
                val task = DownloadTaskBO(
                    item.fileID,
                    item.fileName,
                    "${item.fileID}${item.fileSuffix}",
                    item.fileSize,
                    System.currentTimeMillis(),
                    1
                ) {
                    val entry = item.entity()
                    runOnIO { fileDao.insertFileRecord(entry) }
                    updateImage(entry, holder.download)
                }
                viewModel.dispatchDownloadTask(task)
            }
        }
        holder.view.setOnClickListener {
            context.jumpTo(FileMetaActivity::class.java) {
                putExtra("fileInfo", item.entity())
            }
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

    private fun updateImage(fileEntity: FileEntity?, view: ImageView) {
        if (fileEntity == null) return
        val imageSource = when (fileEntity.fileSuffix) {
            ".apk" -> R.drawable.apk
            ".png", ".jpg" -> R.drawable.image
            ".txt" -> R.drawable.document
            ".pdf" -> R.drawable.pdf
            ".mp3" -> R.drawable.audio
            else -> R.drawable.open
        }
        view.setImageResource(imageSource)
    }

    private fun explore(fileEntity: FileEntity?) {
        if (fileEntity == null) return
        fileExplore(context, fileEntity.fileSuffix, fileEntity.fileName)
    }
}