package com.ljw.secret.network

import com.ljw.secret.Constant.DICTIONARY_API_APPID
import com.ljw.secret.Constant.DICTIONARY_API_APPSECRET
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("dictionary")
    fun getDictionary(
        @Query("content") content: String,
        @Query("app_id") appId: String = DICTIONARY_API_APPID,
        @Query("app_secret") appSecret: String = DICTIONARY_API_APPSECRET
    ): Call<String>
}