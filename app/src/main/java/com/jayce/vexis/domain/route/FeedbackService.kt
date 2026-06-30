package com.jayce.vexis.domain.route

import com.jayce.vexis.util.bean.FeedbackBean
import com.jayce.vexis.core.base.BaseService
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface FeedbackService : BaseService {

    @POST("/sendFeedback")
    @FormUrlEncoded
    fun sendFeedback(
        @Field("feedbackID") feedbackId: String,
        @Field("userID") userID: String,
        @Field("title") title: String,
        @Field("content") content: String,
        @Field("type") type: String
    ): Call<Boolean>

    @GET("/getFeedback")
    fun getFeedback(): Call<ArrayList<FeedbackBean>>

    @POST("/supportFeedback")
    @FormUrlEncoded
    fun supportFeedback(
        @Field("userId") userId: String,
        @Field("feedbackId") feedbackId: String
    ): Call<Boolean>
}