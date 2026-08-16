package com.jayce.vexis.business.file.module

import android.content.Context
import android.graphics.RenderEffect
import android.graphics.Shader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jayce.vexis.client.AndroidTool.adjustText
import com.jayce.vexis.client.BaseTool.envContext
import com.jayce.vexis.client.FileTool.Dir
import com.jayce.vexis.client.FileTool.getDir
import com.jayce.vexis.client.TLog
import com.jayce.vexis.client.ThreadTool
import com.jayce.vexis.client.ThreadTool.ui
import com.jayce.vexis.core.base.BaseAdapter
import com.jayce.vexis.databinding.DynamicDialogBinding
import com.jayce.vexis.databinding.DynamicModuleItemLayoutBinding
import com.jayce.vexis.domain.route.FileService
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.ui.block.FlexibleDialog
import com.jayce.vexis.util.util.FileUtil.getFileHashAndHead
import com.jayce.vexis.util.vo.DynamicVO
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.RandomAccessFile
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class DynamicAdapter(val context: Context, var list: List<DynamicVO>) : BaseAdapter<DynamicVO, DynamicAdapter.ViewHolder>() {

    private val effect = RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.MIRROR)

    override fun getAttachedList() = list

    override fun updateAttachedList(newList: List<DynamicVO>) {
        list = newList
    }

    class ViewHolder(val binding: DynamicModuleItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val root = binding.itemRoot
        val image = binding.image
        val title = binding.title
        val download = binding.download
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DynamicModuleItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.title.text = item.title
        holder.title.adjustText(holder.title.textSize)
        holder.image.setRenderEffect(effect)
        holder.download.strokePercent = 0.25f
        holder.download.type = item.type
        var pair = 0L to File("")
        if (item.type == 1) {
            holder.download.hintText = "点击展开"
        } else {
            pair = checkFileLength(item.path, item.title, item.hash)
            if (pair.first == -2L) {
                holder.image.setRenderEffect(null)
                holder.download.visibility = View.GONE
            }
            if (pair.first > 0) {
                holder.download.progress = pair.first.toFloat() / item.size
                holder.download.hintText = "继续下载"
            }
        }
        val length = pair.first
        holder.download.setOnClickListener {
            if (length < -1) return@setOnClickListener
            if (item.type == 1) {
                downloadItem(position)
                return@setOnClickListener
            }
            val head = "bytes=$length-"
            request<FileService, _>({ downloadFile("apk/${item.path}", head) }){
                download(length, pair.second, it.byteStream()) {
                    holder.download.progress = it.toFloat() / item.size
                    if (it >= item.size) {
                        holder.image.setRenderEffect(null)
                        ui { holder.download.visibility = View.GONE }
                    }
                }
            }
        }
    }

    override fun getItemCount() = list.size

    override fun getItemViewType(position: Int) = position

    private fun checkFileLength(path: String, name: String, hash: String): Pair<Long, File> {
        val savePath = getDir(Dir.LOC_PRIVATE_FILE, envContext)?.path
        if(savePath.isNullOrEmpty()) return -3L to File("")
        val voPath = path.split("/")[0]
        val file = File("$savePath/$voPath/${name}")
        var localLength = 0L
        if (file.exists()) {
            val localHash = getFileHashAndHead(file, "SHA256").first
            if (localHash == hash) {
                return -2L to File("")
            }
            localLength = file.length()
        } else {
            file.parentFile?.mkdirs()
            file.createNewFile()
        }
        return localLength to file
    }

    private fun download(localLength: Long, file: File, stream: InputStream, onProgressUpdate: suspend (Long) -> Unit) {
        val randomAccessFile = RandomAccessFile(file, "rw")
        randomAccessFile.seek(localLength)
        ThreadTool.runOnMulti {
            randomAccessFile.use { accessFile ->
                val bytes = ByteArray(1024)
                var length: Int
                var totalByte = localLength
                while ((stream.read(bytes).also { length = it }) != -1) {
                    accessFile.write(bytes, 0, length)
                    totalByte += length
                    onProgressUpdate(totalByte)
                }
            }
        }
    }

    private fun downloadItem(position: Int) {
        val item = list[position]
        val adapter = DynamicDialogAdapter(context, item.entries) { vo, process ->
            val pair = checkFileLength(item.path, vo.name, vo.hash)
            val length = pair.first
            if (length == -2L) {
                process.progress = 1.0f
            }
            if (length > 0) {
                process.progress = length.toFloat() / vo.size
                process.hintText = "继续下载"
            }
            process.type = 0
            val head = "bytes=$length-"
            process.setOnClickListener {
                if (length < -1) return@setOnClickListener
                val rootBase = "resource/${item.path}"
                val downloadPath = "$rootBase/${vo.name}"
                request<FileService, _>({ downloadFile(downloadPath, head) }){ body ->
                    download(length, pair.second, body.byteStream()) { bytes ->
                        process.progress = bytes.toFloat() / vo.size
                        if (bytes >= vo.size && vo.name.endsWith(".zip")) unzipFile(item.path, vo.name)
                    }
                }
            }
        }
        FlexibleDialog
            .flexibleView<DynamicDialogBinding>(context) {
                rv.layoutManager = LinearLayoutManager(context)
                rv.adapter = adapter
            }
            .title("下载资源")
            .positive("OK") {  }
            .show()
    }

    private suspend fun unzipFile(rootBase: String, filePath: String) {
        val dir = getDir(Dir.LOC_PRIVATE_FILE, envContext)
        val base = File(dir, rootBase)
        if (base.exists().not()) base.mkdirs()
        TLog.d("start unzip")
        ZipInputStream(FileInputStream("$base/$filePath")).use { zis ->
            var zipEntry: ZipEntry
            while (zis.nextEntry.also { zipEntry = it } != null) {
                if (zipEntry.isDirectory) checkDir(base, zipEntry.name)
                else {
                    val writePath = "${base.absolutePath}/${zipEntry.name}"
                    kotlin.runCatching {
                        FileOutputStream(writePath).use { fos ->
                            val buffer = ByteArray(1024)
                            var length = 0
                            while (zis.read(buffer).also { length = it } != -1) {
                                fos.write(buffer, 0, length)
                            }
                        }
                    }.onFailure {
                        TLog.e("unzip error: ${it.message}")
                    }
                }
            }
            TLog.d("unzip all finish!")
        }
    }

    private fun checkDir(base: File, dir: String) {
        val file = File(base, dir)
        if (file.exists().not()) file.mkdirs()
    }
}