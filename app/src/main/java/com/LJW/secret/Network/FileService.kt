package com.ljw.secret.network

import com.google.gson.internal.LinkedTreeMap
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

interface FileService {

    @POST("/fileUpload")
    @Multipart
    fun uploadFile(@Part file: MultipartBody.Part): Call<LinkedHashMap<String, Boolean>>
}