package com.ljw.secret.network

import com.google.gson.internal.LinkedTreeMap
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserService {
    @POST("register")
    @FormUrlEncoded
    fun checkUserName(@FieldMap requestUser: Map<String,String>):Call<LinkedTreeMap<String,Int>>

    @POST("/login")
    @FormUrlEncoded
    fun LoginSystem(@Field("unique") unique:String,@Field("password") password:String):Call<LinkedTreeMap<String,String>>
}