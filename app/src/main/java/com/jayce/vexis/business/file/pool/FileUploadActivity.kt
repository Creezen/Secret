package com.jayce.vexis.business.file.pool

import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.MutableLiveData
import com.jayce.vexis.util.Config.MEDIA_TYPE_ALL
import com.jayce.vexis.util.Config.NIL
import com.jayce.vexis.util.bean.FileBean
import com.jayce.vexis.util.getRandomString
import com.jayce.vexis.util.toTime
import com.jayce.vexis.client.AndroidTool.msg
import com.jayce.vexis.client.AndroidTool.toast
import com.jayce.vexis.client.FileTool.getFilePathByUri
import com.jayce.vexis.client.NetTool.buildFileMultipart
import com.jayce.vexis.client.ThreadTool
import com.jayce.vexis.client.ThreadTool.ui
import com.jayce.vexis.client.ability.thread.BlockOption
import com.jayce.vexis.client.ability.thread.ThreadType
import com.jayce.vexis.R
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.FileUploadBinding
import com.jayce.vexis.domain.route.FileService
import com.jayce.vexis.foundation.Util.request
import kotlinx.coroutines.Dispatchers
import java.io.File

class FileUploadActivity : BaseActivity<FileUploadBinding>() {

    companion object {
        const val SPLIT = "/"
    }

    private var selectedFilePath: String? = null
    private var fileLaunch: ActivityResultLauncher<Array<String>>? = null
    val descTextLivedata = MutableLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() = binding.apply {
        activity = this@FileUploadActivity
        lifecycleOwner = this@FileUploadActivity
        descTextLivedata.value = NIL
        textSize = "0/100"
        descTextLivedata.observe(this@FileUploadActivity) { text ->
            if (text.length > 100) {
                descTextLivedata.value = text.substring(0, 100)
                textSize = "100/100"
            } else {
                textSize = "${text.length}/100"
            }
        }
        selectFile.setOnClickListener {
            fileLaunch?.launch(arrayOf(MEDIA_TYPE_ALL))
        }
        upload.setOnClickListener {
            val filePath = selectedFilePath
            val fileName = selectedFile.msg()
            if (filePath.isNullOrEmpty()) {
                getString(R.string.no_file_select).toast()
                return@setOnClickListener
            }
            getString(R.string.upload_waiting).toast()
            val file = File(filePath)
            val fileID = "${getRandomString(6)}${System.currentTimeMillis()}"
            val fileSuffix = fileName.substring(fileName.lastIndexOf("."))
            val description = description.msg()
            val illustrate = illustrate.msg()
            val uploadTime = System.currentTimeMillis().toTime()
            val fileSize = file.length()
            val option = BlockOption(ThreadType.MULTI, 5000, Dispatchers.IO)
            ThreadTool.runWithBlocking(option) {
                val filePart = buildFileMultipart(filePath, "file")
                val fileBean = FileBean(
                    fileName,
                    fileID,
                    fileSuffix,
                    description,
                    illustrate,
                    fileSize,
                    uploadTime,
                    NIL
                )
                request<FileService, Int>({ uploadFile(fileBean, filePart) }) {
                    when (it) {
                        1 -> finish()
                        else -> getString(R.string.service_error).toast()
                    }
                }
            }
        }
    }

    override fun registerLauncher() {
        fileLaunch = getLauncher(openFile()) {
            it?.let { uri ->
                val filePath = getFilePathByUri(uri)
                if (filePath.isNullOrEmpty()) return@let
                selectedFilePath = filePath
                val splitIdex = filePath.lastIndexOf(SPLIT)
                val displayFileName = filePath.substring(splitIdex + 1)
                binding.selectedFile.setText(displayFileName)
            }
        }
    }
}