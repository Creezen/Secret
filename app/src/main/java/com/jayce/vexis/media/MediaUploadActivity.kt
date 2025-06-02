package com.jayce.vexis.media

import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.MutableLiveData
import com.creezen.commontool.CreezenTool.getRandomString
import com.creezen.commontool.CreezenTool.toTime
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.AndroidTool.workInDispatch
import com.creezen.tool.FileTool.getFilePathByUri
import com.creezen.tool.NetTool
import com.creezen.tool.NetTool.await
import com.creezen.tool.NetTool.buildFileMultipart
import com.creezen.tool.contract.LifecycleJob
import com.jayce.vexis.base.BaseActivity
import com.jayce.vexis.databinding.FileUploadBinding
import java.io.File

class MediaUploadActivity : BaseActivity() {
    companion object {
        const val SPLIT = "/"
        const val TAG = "MediaUploadActivity"
    }

    private var selectedFilePath: String? = null
    private var fileLaunch: ActivityResultLauncher<Array<String>>? = null
    private lateinit var binding: FileUploadBinding
    val descTextLivedata = MutableLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FileUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        with(binding) {
            activity = this@MediaUploadActivity
            lifecycleOwner = this@MediaUploadActivity
            descTextLivedata.value = ""
            textSize = "0/100"
            descTextLivedata.observe(this@MediaUploadActivity) { text ->
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
                    "你还没选择文件哦！".toast()
                    return@setOnClickListener
                }
                "正在上传中，请稍等....".toast()
                val file = File(filePath)
                val fileID = "${getRandomString(6)}${System.currentTimeMillis()}"
                val fileSuffix = fileName.substring(fileName.lastIndexOf("."))
                val description = description.msg()
                val illustrate = illustrate.msg()
                val uploadTime = System.currentTimeMillis().toTime()
                val fileSize = file.length()
                workInDispatch(
                    this@MediaUploadActivity,
                    delayMillis = 5000L,
                    lifecycleJob =
                        object : LifecycleJob {
                            override suspend fun onDispatch() {
                                val filePart = buildFileMultipart(filePath, "file")
                                val result =
                                    NetTool.create<MediaService>().uploadFile(
                                        fileName,
                                        fileID,
                                        fileSuffix,
                                        description,
                                        illustrate,
                                        fileSize,
                                        uploadTime,
                                        filePart,
                                    ).await()
                                if (result["loadResult"] == true) {
                                    finish()
                                }
                            }
                        },
                )
            }
        }
    }

    override fun registerLauncher() {
        fileLaunch =
            getLauncher(openFile()) {
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
