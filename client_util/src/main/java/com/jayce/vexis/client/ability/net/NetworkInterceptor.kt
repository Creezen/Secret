package com.jayce.vexis.client.ability.net

import com.jayce.vexis.client.AndroidTool.toast
import com.jayce.vexis.client.TLog
import com.jayce.vexis.client.ThreadTool.runOnMain
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 备注见NetworkEventListener
 */
class NetworkInterceptor(private val debug: Boolean) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val start = System.currentTimeMillis()
        val response = chain.proceed(request)
        val duration = System.currentTimeMillis() - start
        if (response.code() != 200) {
            runOnMain { "${response.code()}  ${response.message()}".toast() }
        }
        if (!debug) return response
        val connection = chain.connection()
        val address = connection?.route()?.socketAddress()
        var ipInfo = ""
        if (address != null) {
            val ip = address.address.hostAddress
            val port = address.port
            ipInfo = "[$ip:$port] "
        }
        TLog.d("$ipInfo(${duration}ms): ${request.url()} -- ${response.code()} -- ${response.message()}")
        return response
    }
}