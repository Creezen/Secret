package com.ljw.secret.network

import com.ljw.secret.bean.FeedbackBean
import com.ljw.secret.bean.FeedbackItem
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
    ): Call<LinkedHashMap<String, Boolean>>

    @GET("/getFeedback")
    fun getFeedback(): Call<LinkedHashMap<String, ArrayList<FeedbackItem>>>
}