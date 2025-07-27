package com.jayce.vexis.business.login

import retrofit2.Call
import retrofit2.http.POST

interface PackageService {
    @POST("checkVersion")
    fun getVersion(): Call<ApkSimpleInfo>
}