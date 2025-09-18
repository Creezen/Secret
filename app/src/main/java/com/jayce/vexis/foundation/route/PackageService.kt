package com.jayce.vexis.foundation.route

import com.creezen.commontool.bean.ApkSimpleInfo
import com.jayce.vexis.core.base.BaseService
import retrofit2.Call
import retrofit2.http.POST

interface PackageService : BaseService {
    @POST("checkVersion")
    fun getVersion(): Call<ApkSimpleInfo>
}