package com.jayce.vexis.domain.route

import com.jayce.vexis.core.base.BaseService
import com.jayce.vexis.util.bean.FileBean
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Streaming

interface FileService : BaseService {
    @POST("/fileUpload")
    @Multipart
    fun uploadFile(
        @Part("fileEntry") fileBean: FileBean,
        @Part file: MultipartBody.Part,
    ): Call<Int>

    @POST("/fileFetch")
    fun fetchFile(): Call<List<FileBean>>

    @Streaming
    @GET("file/{fileName}")
    fun downloadFile(@Path("fileName") fileName: String): Call<ResponseBody>
}