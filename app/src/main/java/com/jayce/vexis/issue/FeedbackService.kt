package com.jayce.vexis.issue

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface FeedbackService {

    @POST("/sendFeedback")
    @FormUrlEncoded
    fun sendFeedback(@Field("userID") userID: String,
                     @Field("title") title: String,
                     @Field("content") content: String
    ): Call<Boolean>

    @GET("/getFeedback")
    fun getFeedback(): Call<LinkedHashMap<String, ArrayList<FeedbackItem>>>
}
