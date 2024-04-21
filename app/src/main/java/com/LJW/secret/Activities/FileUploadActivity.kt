package com.ljw.secret.activities

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.ljw.secret.databinding.FileUploadBinding
import com.ljw.secret.util.DataUtil.getRandomString
import com.ljw.secret.util.DataUtil.toast
import com.ljw.secret.util.FileUtil
import com.ljw.secret.util.NetUtil
import com.ljw.secret.util.DataUtil.msg
import com.ljw.secret.util.DataUtil.toTime
import kotlinx.coroutines.launch
import java.io.File

class FileUploadActivity : BaseActivity() {

    companion object{
        const val SPLIT = "/"
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
                    "你还没选择文件哦！".toast()
                    return@setOnClickListener
                }
                val file = File(filePath)
                val fileID = "${getRandomString(6)}${System.currentTimeMillis()}"
                val fileSuffix = fileName.substring(fileName.lastIndexOf("."))
                val description = description.msg()
                val illustrate = illustrate.msg()
                val uploadTime = System.currentTimeMillis().toTime()
                val fileSize = file.length()
                lifecycleScope.launch {
                    val result = NetUtil.uploadFile(
                        filePath, fileName, fileID, fileSuffix, description,
                        illustrate, uploadTime, fileSize, file
                    )
                    Log.e("ListPage.registerLauncher","filepath: $result")
                }
            }
        }
    }

    override fun registerLauncher() {
        fileLaunch = getLauncher(openFile()) {
            it?.let { uri ->
                val filePath = FileUtil.getFilePathByUri(uri)
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