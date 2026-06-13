package com.creezen.tool.ability.net

import com.creezen.tool.TLog
import okhttp3.Call
import okhttp3.EventListener
import okhttp3.Handshake
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy

class NetworkEventListener(private val debug: Boolean) : EventListener() {

    private var callStart: Long = 0L
    private var dnsStart: Long = 0L
    private var tcpConnectionStart: Long = 0L
    private var tlsConnectionStart: Long = 0L
    private var requestHeaderStart: Long = 0L
    private var requestBodyStart: Long = 0L
    private var responseHeaderStart: Long = 0L
    private var responseBodyStart: Long = 0L

    private var callDuration: Long = 0L
    private var dnsDuration: Long = 0L
    private var tcpConnectionDuration: Long = 0L
    private var tlsConnectionDuration: Long = 0L
    private var requestHeaderDuration: Long = 0L
    private var requestBodyDuration: Long = 0L
    private var responseHeaderDuration: Long = 0L
    private var responseBodyDuration: Long = 0L

    override fun callStart(call: Call) {
        super.callStart(call)
        callStart = now()
    }

    override fun dnsStart(call: Call, domainName: String) {
        super.dnsStart(call, domainName)
        dnsStart = now()
    }

    override fun dnsEnd(call: Call, domainName: String, inetAddressList: MutableList<InetAddress>) {
        super.dnsEnd(call, domainName, inetAddressList)
        dnsDuration = now() - dnsStart
    }

    override fun connectStart(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy) {
        super.connectStart(call, inetSocketAddress, proxy)
        tcpConnectionStart = now()
    }

    override fun connectEnd(call: Call, address: InetSocketAddress, proxy: Proxy, protocol: Protocol?) {
        super.connectEnd(call, address, proxy, protocol)
        tcpConnectionDuration = now() - tcpConnectionStart
    }

    override fun secureConnectStart(call: Call) {
        super.secureConnectStart(call)
        tlsConnectionStart = now()
    }

    override fun secureConnectEnd(call: Call, handshake: Handshake?) {
        super.secureConnectEnd(call, handshake)
        tlsConnectionDuration = now()  - tlsConnectionStart
    }

    override fun requestHeadersStart(call: Call) {
        super.requestHeadersStart(call)
        requestHeaderStart = now()
    }

    override fun requestHeadersEnd(call: Call, request: Request) {
        super.requestHeadersEnd(call, request)
        requestHeaderDuration = now() - requestHeaderStart
    }

    override fun requestBodyStart(call: Call) {
        super.requestBodyStart(call)
        requestBodyStart = now()
    }

    override fun requestBodyEnd(call: Call, byteCount: Long) {
        super.requestBodyEnd(call, byteCount)
        requestBodyDuration = now() - requestBodyStart
    }

    override fun responseHeadersStart(call: Call) {
        super.responseHeadersStart(call)
        responseHeaderStart = now()
    }

    override fun responseHeadersEnd(call: Call, response: Response) {
        super.responseHeadersEnd(call, response)
        responseHeaderDuration = now() - responseHeaderStart
    }

    override fun responseBodyStart(call: Call) {
        super.responseBodyStart(call)
        responseBodyStart = now()
    }

    override fun responseBodyEnd(call: Call, byteCount: Long) {
        super.responseBodyEnd(call, byteCount)
        responseBodyDuration = now() - responseBodyStart
    }

    /**
     * 链路为： DNS -》 TCP（其中包括TLS时间） -》 requestHeader -》 requestBody -》 responseHeader -》 responseBody
     * 对应的方法为：
     *      DNS： dnsStart/dnsEnd
     *      TCP： connectStart/connectEnd  （中间会执行TLS  secureConnectStart/secureConnectEnd）
     *      requestHeader： requestHeadersStart/requestHeadersEnd
     *      requestBody： requestBodyStart/requestBodyEnd
     *      responseHeader： responseHeadersStart/responseHeadersEnd
     *      responseBody： responseBodyStart/responseBodyEnd
     *
     *  所以，callDuration的时间约等于：DNS + TCP + requestHeader + requestBody + responseHeader + responseBody
     *  误差数据各阶段之间的耗时和其他的一些影响
     *
     *  NetworkInterceptor中拦截的从发送请求到收到回复的时间，约等于处理 responseHeader + responseBody 的时间
     */
    override fun callEnd(call: Call) {
        super.callEnd(call)
        callDuration = now() - callStart
        if (!debug) return
        TLog.d( "\ncallDuration:  $callDuration  \n"+
                "DNS:$dnsDuration   " +
                "TCP:$tcpConnectionDuration(TLS:$tlsConnectionDuration)  \n" +
                "requestHeader:$requestHeaderDuration  " +
                "requestBody:$requestBodyDuration  \n" +
                "responseHeader:$responseHeaderDuration  "+
                "responseBody:$responseBodyDuration\n")
    }

    override fun callFailed(call: Call, ioe: IOException) {
        super.callFailed(call, ioe)
        TLog.d("callFailed: ${ioe.message}")
    }

    private fun resetData() {
        callStart = 0L
        dnsStart = 0L
        tcpConnectionStart = 0L
        tlsConnectionStart = 0L
        requestHeaderStart = 0L
        requestBodyStart = 0L
        responseHeaderStart = 0L
        responseBodyStart = 0L

        callDuration = 0L
        dnsDuration = 0L
        tcpConnectionDuration = 0L
        tlsConnectionDuration = 0L
        requestHeaderDuration = 0L
        requestBodyDuration = 0L
        responseHeaderDuration = 0L
        responseBodyDuration = 0L
    }

    private fun now() = System.nanoTime() / 1000000
}