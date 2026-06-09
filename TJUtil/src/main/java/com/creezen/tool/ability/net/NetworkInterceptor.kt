package com.creezen.tool.ability.net

import com.creezen.tool.TLog
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 备注见NetworkEventListener
 */
class NetworkInterceptor(private val debug: Boolean) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!debug) return chain.proceed(request)
        val start = System.currentTimeMillis()
        val response = chain.proceed(request)
        val connection = chain.connection()
        val address = connection?.route()?.socketAddress()
        var ipInfo = ""
        if (address != null) {
            val ip = address.address.hostAddress
            val port = address.port
            ipInfo = "[$ip:$port] "
        }
        val duration = System.currentTimeMillis() - start
        TLog.d("$ipInfo(${duration}ms): ${request.url()} -- ${response.code()} -- ${response.message()}")
        return response
    }
}