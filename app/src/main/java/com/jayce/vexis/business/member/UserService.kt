package com.jayce.vexis.business.member

import com.google.gson.internal.LinkedTreeMap
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UserService {
    @POST("register")
    @FormUrlEncoded
    fun checkUserName(
        @FieldMap requestUser: Map<String, String>,
    ): Call<LinkedTreeMap<String, Int>>

    @POST("/login")
    @FormUrlEncoded
    fun loginSystem(
        @Field("unique") unique: String,
        @Field("password") password: String,
    ): Call<LinkedTreeMap<String, String>>

    @POST("/postAvatar")
    @Multipart
    fun uploadAvatar(
        @Part("userID") userId: String,
        @Part filePart: MultipartBody.Part,
    ): Call<LinkedTreeMap<String, Boolean>>

    @POST("/getAllUser")
    fun getAllUser(): Call<LinkedTreeMap<String, List<ActiveItem>>>

    @POST("/managerUser")
    @FormUrlEncoded
    fun manageUser(
        @Field("operation") operation: Int,
        @Field("userId") userId: String,
    ): Call<LinkedTreeMap<String, Boolean>>
}
