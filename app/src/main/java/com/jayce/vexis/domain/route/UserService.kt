package com.jayce.vexis.domain.route

import com.jayce.vexis.core.base.BaseService
import com.jayce.vexis.util.dto.UserDTO
import com.jayce.vexis.util.vo.ActiveVO
import com.jayce.vexis.util.vo.StatusVO
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface UserService : BaseService {

    @POST("register")
    @Headers("Content-Type: application/json")
    fun register(
        @Body requestUser: UserDTO,
        @Query("code") code: String
    ): Call<StatusVO>

    @POST("/login")
    @FormUrlEncoded
    fun loginSystem(@Field("unique") unique: String, @Field("password") password: String): Call<StatusVO>

    @POST("/postAvatar")
    @Multipart
    fun uploadAvatar(@Part("userID") userId: String, @Part filePart: MultipartBody.Part): Call<Boolean>

    @POST("/getAllUser")
    fun getAllUser(): Call<List<ActiveVO>>

    @POST("/setUserAsAdmin")
    @FormUrlEncoded
    fun setUserAsAdmin(@Field("userId") userId: String): Call<Boolean>

    @POST("/deleteUser")
    @FormUrlEncoded
    fun deleteUser(@Field("userId") userId: String): Call<Boolean>

    @POST("/followUser")
    @FormUrlEncoded
    fun followUser(
        @Field("fansId") fansId: String,
        @Field("userId") userId: String
    ): Call<Int>

    @POST("/sendEmailCode")
    @FormUrlEncoded
    fun sendEmailCode(
        @Field("id") userId: String,
        @Field("email") email: String
    ): Call<StatusVO>
}