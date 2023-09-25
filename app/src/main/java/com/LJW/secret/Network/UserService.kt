package com.LJW.secret.Network

import com.google.gson.internal.LinkedTreeMap
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface UserService {
    @POST("register")
    @FormUrlEncoded
    fun checkUserName(@FieldMap requestUser: Map<String,String>):Call<LinkedTreeMap<String,Int>>
}