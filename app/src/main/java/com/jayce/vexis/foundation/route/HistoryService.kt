package com.jayce.vexis.foundation.route

import com.jayce.vexis.foundation.base.BaseService
import com.jayce.vexis.foundation.bean.HistoryEntry
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface HistoryService : BaseService {

    @POST("/sendEvent")
    @FormUrlEncoded
    fun sendEventData(
        @Field("time") time: String,
        @Field("event") event: String,
    ): Call<Boolean>

    @POST("/queryAllEvent")
    fun queryAllEvent(): Call<List<HistoryEntry>>

}