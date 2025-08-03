package com.jayce.vexis.foundation.route

import com.jayce.vexis.foundation.base.BaseService
import com.jayce.vexis.foundation.bean.FeedbackEntry
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface FeedbackService : BaseService {

    @POST("/sendFeedback")
    @FormUrlEncoded
    fun sendFeedback(
        @Field("userID") userID: String,
        @Field("title") title: String,
        @Field("content") content: String,
    ): Call<Boolean>

    @GET("/getFeedback")
    fun getFeedback(): Call<LinkedHashMap<String, ArrayList<FeedbackEntry>>>
}