package com.jayce.vexis.business.history

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface HistoryService {

    @POST("/sendEvent")
    @FormUrlEncoded
    fun sendEventData(
        @Field("time") time: String,
        @Field("event") event: String,
    ): Call<Boolean>

}