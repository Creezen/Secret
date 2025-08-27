package com.jayce.vexis.foundation.route

import com.jayce.vexis.core.Config.DICTIONARY_API_APPID
import com.jayce.vexis.core.Config.DICTIONARY_API_APPSECRET
import com.jayce.vexis.core.base.BaseService
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService : BaseService {
    @GET("dictionary")
    fun getDictionary(
        @Query("content") content: String,
        @Query("app_id") appId: String = DICTIONARY_API_APPID,
        @Query("app_secret") appSecret: String = DICTIONARY_API_APPSECRET,
    ): Call<String>
}