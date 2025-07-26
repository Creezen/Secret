package com.jayce.vexis.business.peer

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface PeerService {

    @FormUrlEncoded
    @POST("/postAdvice")
    fun sendSeniorAdvice(
        @Field("primary") primary: String,
        @Field("second") second: String,
        @Field("tertiary") tertiary: String,
        @Field("content") content: String,
    ): Call<Boolean>
}