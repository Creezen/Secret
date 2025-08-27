package com.jayce.vexis.foundation.route

import com.jayce.vexis.core.base.BaseService
import com.jayce.vexis.foundation.bean.ApkSimpleEntry
import retrofit2.Call
import retrofit2.http.POST

interface PackageService : BaseService {
    @POST("checkVersion")
    fun getVersion(): Call<ApkSimpleEntry>
}