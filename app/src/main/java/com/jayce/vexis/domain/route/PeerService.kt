package com.jayce.vexis.domain.route

import com.jayce.vexis.util.bean.PeerAdviceBean
import com.jayce.vexis.core.base.BaseService
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface PeerService : BaseService {

    @POST("/postAdvice")
    @Headers("Content-Type: application/json")
    fun sendSeniorAdvice(@Body peerAdviceBean: PeerAdviceBean): Call<Boolean>

    @FormUrlEncoded
    @POST("/getAdvice")
    fun getAdvice(
        @Field("primary") primary: String,
        @Field("second") second: String,
        @Field("tertiary") tertiary: String,
    ): Call<List<PeerAdviceBean>>
}