package com.jayce.vexis.foundation.route

import com.creezen.commontool.bean.FileBean
import com.jayce.vexis.core.base.BaseService
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
    ): Call<LinkedHashMap<String, Boolean>>

    @POST("/fileFetch")
    fun fetchFile(): Call<LinkedHashMap<String, List<FileBean>>>

    @Streaming
    @GET("FileSystem/{fileName}")
    fun downloadFile(
        @Path("fileName") fileName: String,
    ): Call<ResponseBody>
}