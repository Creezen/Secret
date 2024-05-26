package com.ljw.secret.network

import com.google.gson.internal.LinkedTreeMap
import com.ljw.secret.bean.ResourceItem
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Streaming
import java.io.File

interface FileService {

    @POST("/fileUpload")
    @Multipart
    fun uploadFile(
        @Part("fileName") fileName: String,
        @Part("fileID") fileID: String,
        @Part("fileSuffix") fileSuffix: String,
        @Part("description") description: String,
        @Part("illustrate") illustrate: String,
        @Part("fileSize") fileSize: Long,
        @Part("uploadTime") uploadTime: String,
        @Part file: MultipartBody.Part
    ): Call<LinkedHashMap<String, Boolean>>

    @POST("/fileFetch")
    fun fetchFile(): Call<LinkedHashMap<String, List<ResourceItem>>>

    @Streaming
    @GET("FileSystem/{fileName}")
    fun downloadFile(@Path("fileName") fileName: String): Call<ResponseBody>
}