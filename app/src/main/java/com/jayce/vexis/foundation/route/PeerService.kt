package com.jayce.vexis.foundation.route

import com.jayce.vexis.foundation.base.BaseService
import com.jayce.vexis.foundation.bean.PeerAdviceEntry
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface PeerService : BaseService {

    @FormUrlEncoded
    @POST("/postAdvice")
    fun sendSeniorAdvice(
        @Field("primary") primary: String,
        @Field("second") second: String,
        @Field("tertiary") tertiary: String,
        @Field("content") content: String,
    ): Call<Boolean>

    @FormUrlEncoded
    @POST("/getAdvice")
    fun getAdvice(
        @Field("primary") primary: String,
        @Field("second") second: String,
        @Field("tertiary") tertiary: String,
    ): Call<List<PeerAdviceEntry>>
}