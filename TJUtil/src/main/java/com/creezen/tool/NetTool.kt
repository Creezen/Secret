package com.creezen.tool

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Key
import com.bumptech.glide.request.RequestOptions
import com.creezen.commontool.CreezenParam.EVENT_TYPE_DEFAULT
import com.creezen.commontool.CreezenParam.EVENT_TYPE_GAME
import com.creezen.commontool.CreezenParam.EVENT_TYPE_MESSAGE
import com.creezen.commontool.CreezenParam.EVENT_TYPE_NOTIFY
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.Constant.BASE_URL
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.util.Arrays
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume

object NetTool {

    private val COOKIE_LIST:MutableList<Cookie> = mutableListOf()
    private val COOKIE_MAP:MutableMap<String,Long> = mutableMapOf()

    private lateinit var onlineSocket : Socket
    private val socketFlag = AtomicBoolean(true)

    val socketReader: BufferedReader by lazy {
        BufferedReader(InputStreamReader(onlineSocket.getInputStream(), "UTF-8"))
    }

    private val socketWriter: BufferedWriter by lazy {
        BufferedWriter(OutputStreamWriter(onlineSocket.getOutputStream(), "UTF-8"))
    }

    private val cookieJar = object : CookieJar {
        override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
            COOKIE_LIST.removeIf { it.name() == "JSESSIONID" }
            COOKIE_LIST.add(cookies.last { it.name() == "JSESSIONID" })
            cookies.forEach {
                if (!it.name().equals("JSESSIONID")) COOKIE_LIST.add(it)
            }
        }

        override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
            if (COOKIE_MAP["firstTime"] == null) {
                val currentTime = System.currentTimeMillis()
                COOKIE_LIST.add(buildCookie("firstTime","${currentTime}"))
                COOKIE_LIST.add(buildCookie("lastTime","${currentTime}"))
                COOKIE_MAP["firstTime"] = currentTime
                COOKIE_MAP["lastTime"] = currentTime
            } else {
                COOKIE_LIST.removeIf { it.name() == "lastTime" }
                COOKIE_LIST.add(buildCookie("lastTime","${System.currentTimeMillis()}"))
            }
            return COOKIE_LIST
        }
    }

    private val okHttp by lazy {
        OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .protocols(Arrays.asList(Protocol.HTTP_1_1))
            .readTimeout(60000, TimeUnit.SECONDS)
            .build()
    }

    val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(okHttp)
            .build()
    }

    val apiRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constant.API_BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    inline fun <reified T> create(): T = retrofit.create(T::class.java)

    inline fun <reified T> createApi(): T = apiRetrofit.create(T::class.java)

    fun setImage(
        context: Context,
        image: ImageView,
        url: String,
        placeHolder: Drawable? = null,
        key: Key?,
        isCircle: Boolean = false
    ) {
        val load = Glide.with(context).load(url)
        var holderBuilder = load
        if (placeHolder != null) {
            holderBuilder = load.placeholder(placeHolder)
        }
        if (key == null) {
            holderBuilder.into(image)
            return
        }
        holderBuilder.apply(getRequestOptions(key, isCircle)).into(image)
    }

    private fun getRequestOptions(key: Key, isCircle: Boolean = false): RequestOptions {
        return if (isCircle) {
            RequestOptions().signature(key).circleCrop()
        } else {
            RequestOptions().signature(key)
        }
    }

    private fun buildCookie(name:String, value:String, domain: String = "www"): Cookie {
        return Cookie.Builder()
            .name(name)
            .value(value)
            .domain(domain)
            .build()
    }

    suspend fun <T : Any> Call<T>.await():T {
        return suspendCancellableCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(p0: Call<T>, p1: Response<T>) {
                    val body = p1.body()
                    if(body != null) {
                        continuation.resume(body)
                    } else {
                        "服务当前离线".toast()
                    }
                }

                override fun onFailure(p0: Call<T>, p1: Throwable) {
                    "网络请求失败".toast()
                }
            })
        }
    }

    fun buildFileMultipart(
        filePath: String,
        paramName: String,
        mediaTypeString: String = "multipart/form-data"
    ) : MultipartBody.Part {
        val fileBody = RequestBody.create(MediaType.parse(mediaTypeString), File(filePath))
        return MultipartBody.Part.createFormData(paramName, filePath, fileBody)
    }

    fun sendMessage(lifecycleOwner: LifecycleOwner, msg: String) {
        if(onlineSocket.isOutputShutdown || onlineSocket.isClosed || socketFlag.get().not()) {
            socketFlag.set(false)
            "与服务器连接失败，请检查网络".toast()
            return
        }
        lifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                kotlin.runCatching {
                    if(msg.isEmpty()) {
                        return@runCatching
                    }
                    socketWriter.write("$msg\n")
                    socketWriter.flush()
                }.onFailure {
                    destroySocket()
                    Log.e("NetTool.sendMessage","send sockeMessage error: $it")
                }
            }
        }
    }

    fun sendDefaultMessage(lifecycleOwner: LifecycleOwner, msg: String) {
        sendMessage(lifecycleOwner, "$EVENT_TYPE_DEFAULT#$msg")
    }

    fun sendChatMessage(lifecycleOwner: LifecycleOwner, msg: String) {
        sendMessage(lifecycleOwner, "$EVENT_TYPE_MESSAGE#$msg")
    }

    fun sendNotifyMessage(lifecycleOwner: LifecycleOwner, msg: String) {
        sendMessage(lifecycleOwner, "$EVENT_TYPE_NOTIFY#$msg")
    }

    fun sendGameMessage(lifecycleOwner: LifecycleOwner, msg: String) {
        sendMessage(lifecycleOwner, "$EVENT_TYPE_GAME#$msg")
    }

    fun sendAckMessage(
        lifecycleOwner: LifecycleOwner,
        msg: String,
        onReceiveMessage: (String) -> Boolean
    ) {
        sendDefaultMessage(lifecycleOwner, msg)
        openMessageReceiver(lifecycleOwner, onReceiveMessage)
    }

    private fun openMessageReceiver(
        lifecycleOwner: LifecycleOwner,
        onReceiveMessage: (String) -> Boolean
    ) {
        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            while(true) {
                val line = socketReader.readLine()
                if (onlineSocket.isClosed || line.isNullOrEmpty()|| socketFlag.get().not()) {
                    socketFlag.set(false)
                    Log.e("ChatActivity.initSocket","服务器错误")
                    break
                }
                if(onReceiveMessage(line).not()) {
                    socketFlag.set(false)
                    destroySocket()
                    break
                }
            }
        }
    }

    fun setOnlineSocket(socket: Socket) {
        onlineSocket = socket
    }

    fun destroySocket() {
        onlineSocket.shutdownOutput()
        onlineSocket.shutdownInput()
        onlineSocket.close()
    }

}