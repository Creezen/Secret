package com.jayce.vexis.login

import com.creezen.tool.Constant.DICTIONARY_API_APPID
import com.creezen.tool.Constant.DICTIONARY_API_APPSECRET
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("dictionary")
    fun getDictionary(
        @Query("content") content: String,
        @Query("app_id") appId: String = DICTIONARY_API_APPID,
        @Query("app_secret") appSecret: String = DICTIONARY_API_APPSECRET,
    ): Call<String>
}
