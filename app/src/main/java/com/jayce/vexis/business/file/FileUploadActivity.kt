package com.jayce.vexis.business.file

import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.MutableLiveData
import com.creezen.commontool.getRandomString
import com.creezen.commontool.toTime
import com.creezen.commontool.bean.FileBean
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.AndroidTool.workInDispatch
import com.creezen.tool.FileTool.getFilePathByUri
import com.creezen.tool.NetTool.buildFileMultipart
import com.creezen.tool.contract.LifecycleJob
import com.jayce.vexis.R
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.FileUploadBinding
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.route.FileService
import kotlinx.coroutines.Dispatchers
import java.io.File

class FileUploadActivity : BaseActivity<FileUploadBinding>() {

    companion object {
        const val SPLIT = "/"
        const val TAG = "MediaUploadActivity"
    }

    private var selectedFilePath: String? = null
    private var fileLaunch: ActivityResultLauncher<Array<String>>? = null
    val descTextLivedata = MutableLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        with(binding) {
            activity = this@FileUploadActivity
            lifecycleOwner = this@FileUploadActivity
            descTextLivedata.value = ""
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
                fileLaunch?.launch(arrayOf("*/*"))
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
                workInDispatch(this@FileUploadActivity,  5000L, Dispatchers.Default, object : LifecycleJob {
                    override suspend fun onDispatch() {
                        val filePart = buildFileMultipart(filePath, "file")
                        val fileBean = FileBean(
                            fileName,
                            fileID,
                            fileSuffix,
                            description,
                            illustrate,
                            fileSize,
                            uploadTime,
                            ""
                            )
                        request<FileService, LinkedHashMap<String, Boolean>>({
                            uploadFile(fileBean, filePart)
                        }) {
                            if (it["loadResult"] == true) {
                                finish()
                            }
                        }
                    }
                })
            }
        }
    }

    override fun registerLauncher() {
        fileLaunch = getLauncher(openFile()) {
                it?.let { uri ->
                    val filePath = getFilePathByUri(uri)
                    if (filePath.isNullOrEmpty()) {
                        return@let
                    }
                    selectedFilePath = filePath
                    val splitIdex = filePath.lastIndexOf(SPLIT)
                    val displayFileName = filePath.substring(splitIdex + 1)
                    binding.selectedFile.setText(displayFileName)
                }
            }
    }
}