package com.ljw.secret.util

import com.ljw.secret.network.FileService
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import java.io.File

object NetUtil {

    /**
     * 必须在子线程中调用
     */
    suspend fun uploadFile(
        filePath: String,
        fileName: String,
        fileID: String,
        fileSuffix: String,
        description: String,
        illustrate: String,
        uploadTime: String,
        fileSize: Long,
        file: File
    ): Boolean? {
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("file", filePath, requestFile)
        val result = NetworkServerCreator.create<FileService>()
            .uploadFile(fileName, fileID, fileSuffix, description, illustrate, fileSize, uploadTime, body)
            .await()
        return result["loadResult"]
    }
}