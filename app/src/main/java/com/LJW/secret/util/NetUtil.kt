package com.ljw.secret.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ljw.secret.network.AvatarSignnature
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

    fun buildFileMultipart(
        filePath: String,
        paramName: String,
        mediaTypeString: String = "multipart/form-data"
    ) : MultipartBody.Part {
        val fileBody = RequestBody.create(MediaType.parse(mediaTypeString), File(filePath))
        return MultipartBody.Part.createFormData(paramName, filePath, fileBody)
    }

    fun getRequestOptions(key: String): RequestOptions {
        return RequestOptions()
            .signature(
                AvatarSignnature(key)
            )
    }

    fun setImage(
        context: Context,
        image: ImageView,
        url: String,
        placeHolder: Drawable? = null,
        cacheKey: String? = null
    ) {
        val load = Glide.with(context).load(url)
        var holderBuilder = load
        if (placeHolder != null) {
            holderBuilder = load.placeholder(placeHolder)
        }
        if (cacheKey == null) {
            holderBuilder.into(image)
            return
        }
        holderBuilder.apply(getRequestOptions(cacheKey)).into(image)
    }
}