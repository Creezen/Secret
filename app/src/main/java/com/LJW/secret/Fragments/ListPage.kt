package com.ljw.secret.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.lifecycleScope
import com.ljw.secret.databinding.MainFunctionsBinding
import com.ljw.secret.network.FileService
import com.ljw.secret.util.FileUtil
import com.ljw.secret.util.NetworkServerCreator
import com.ljw.secret.util.await
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ListPage : BaseFragment() {

    private var fileLaunch: ActivityResultLauncher<Array<String>>? = null
    private var readExternalLaunch: ActivityResultLauncher<Intent>? = null

    override fun registerLauncher() {
        fileLaunch = getLauncher(openFile()) {
            it?.let { uri ->
                lifecycleScope.launch {
                    val filePath = FileUtil.getFilePathByUri(uri)
                    if (filePath.isNullOrEmpty()) {
                        Log.e("ListPage.registerLauncher","file not exist")
                        return@launch
                    }
                    val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), File(filePath))
                    val body = MultipartBody.Part.createFormData("file", filePath, requestFile)
                    val result = NetworkServerCreator.create<FileService>()
                        .uploadFile(body)
                        .await()
                    Log.e("ListPage.registerLauncher","filepath: $result")
                }
            }
        }
        readExternalLaunch = getLauncher(startActivity()) {
            Log.e("ListPage.registerLauncher","$it")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding=MainFunctionsBinding.inflate(inflater)
        binding.toFileUpLoad.setOnClickListener {
            if (Environment.isExternalStorageManager()) {
                selectFile()
            } else {
                readExternalLaunch?.launch(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).also {
                    it.data = Uri.parse("package:" + activity?.packageName)
                })
            }
        }
        return binding.root
    }

    private fun selectFile() {
        fileLaunch?.launch(arrayOf("*/*"))
    }
}