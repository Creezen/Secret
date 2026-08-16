package com.jayce.vexis.domain.route

import com.jayce.vexis.core.base.BaseService
import com.jayce.vexis.util.dto.FileDTO
import com.jayce.vexis.util.vo.DynamicVO
import com.jayce.vexis.util.vo.FileVO
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

interface FileService : BaseService {
    @POST("/fileUpload")
    @Multipart
    fun uploadFile(
        @Part("fileEntry") fileBean: FileDTO,
        @Part file: MultipartBody.Part,
    ): Call<Int>

    @POST("/fileFetch")
    fun fetchFile(): Call<List<FileVO>>

    @Streaming
    @GET("file/{fileName}")
    fun downloadFile(
        @Path("fileName") fileName: String,
        @Header("Range") range: String
    ): Call<ResponseBody>

    @GET("loadSlider")
    fun loadSlider(): Call<List<String>>

    @GET("getDynamic")
    fun getDynamicModule(): Call<List<DynamicVO>>

    @GET("loadDynamicSubModule")
    fun loadDynamicSubModule(
        @Query("module") module: String,
        @Query("sunModule") sunModule: String
    ): Call<List<DynamicVO>>
}