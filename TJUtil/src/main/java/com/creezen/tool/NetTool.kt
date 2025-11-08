package com.creezen.tool

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.RequestOptions
import com.creezen.commontool.Config.EventType.EVENT_TYPE_DEFAULT
import com.creezen.commontool.Config.EventType.EVENT_TYPE_GAME
import com.creezen.commontool.Config.EventType.EVENT_TYPE_MESSAGE
import com.creezen.commontool.Config.EventType.EVENT_TYPE_NOTIFY
import com.creezen.commontool.Config.NetworkParam.COOKIE_USER_ID
import com.creezen.commontool.Config.NetworkParam.COOKIE_UUID
import com.creezen.commontool.Config.SERVER_DOMAIN
import com.creezen.commontool.bean.TelecomBean
import com.creezen.commontool.bean.UserBean
import com.creezen.commontool.toJson
import com.creezen.tool.AndroidTool.readPrefs
import com.creezen.tool.AndroidTool.toast
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
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
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.util.Arrays
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.net.ssl.HostnameVerifier
import kotlin.coroutines.resume

object NetTool {

    private const val TAG = "NetTool"

    private lateinit var baseUrl: String
    private var socketPort: Int = 0
    private lateinit var baseSocketPath: String
    private lateinit var onlineSocket : Socket
    private var user: UserBean? = null
    private val socketFlag = AtomicBoolean(true)
    private var socketReader: BufferedReader? = null
    private var socketWriter: BufferedWriter? = null

    private val cookieJar = object : CookieJar {
        override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) { /**/ }

        override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
            val cookieList = arrayListOf<Cookie>()
            user?.apply {
                cookieList.add(buildCookie(COOKIE_USER_ID, userId))
                cookieList.add(buildCookie(COOKIE_UUID, session))
            }
            return cookieList
        }
    }

    private fun buildCookie(name: String, value: String) =
        Cookie.Builder()
            .name(name)
            .value(value)
            .domain(SERVER_DOMAIN)
            .build()

    private val okHttp by lazy {
        OkHttpClient.Builder()
            .hostnameVerifier(HostnameVerifier{ _, _ ->
                return@HostnameVerifier true
            })
            .cookieJar(cookieJar)
            .protocols(Arrays.asList(Protocol.HTTP_1_1))
            .readTimeout(60000, TimeUnit.SECONDS)
            .build()
    }

    val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(okHttp)
            .build()
    }

    inline fun <reified T> create(): T = retrofit.create(T::class.java)

    fun setUser(currentUser: UserBean) {
        user = currentUser
    }

    fun init(initParam: BaseTool.InitParam) {
        baseUrl = initParam.baseUrl
        baseSocketPath = initParam.baseSocketPath
        socketPort = initParam.socketPort
        Glide.get(BaseTool.env()).registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(okHttp)
        )
    }

    fun setImage(
        context: Context,
        image: ImageView,
        url: String,
        placeHolder: Drawable? = null,
        key: Key? = null,
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

    suspend fun <T> Call<T>.await():T {
        return suspendCancellableCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(p0: Call<T>, p1: Response<T>) {
                    val body = p1.body()
                    if(body != null) {
                        continuation.resume(body)
                    } else {
                        "服务器返回错误!".toast()
                    }
                }

                override fun onFailure(p0: Call<T>, p1: Throwable) {
                    Log.d(TAG, "net error: ${p1.message}")
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

    fun sendMessage(scope: CoroutineScope, msg: TelecomBean) {
        if(onlineSocket.isOutputShutdown || onlineSocket.isClosed || socketFlag.get().not()) {
            socketFlag.set(false)
            "与服务器连接失败，请检查网络".toast()
            return
        }
        scope.launch {
            kotlin.runCatching {
                if(msg.content.isEmpty()) {
                    return@runCatching
                }
                Log.d(TAG, "send message: $msg")
                socketWriter?.write("${msg.toJson()}\n")
                socketWriter?.flush()
            }.onFailure {
                Log.d(TAG, "sendMessage error: ${it.message}")
                destroySocket()
                reConnect(msg)
            }
        }
    }

    fun sendDefaultMessage(scope: CoroutineScope, msg: String) {
        user?.apply {
            val message = buildTeleMessage(EVENT_TYPE_DEFAULT, msg, this)
            sendMessage(scope, message)
        }
    }

    fun sendChatMessage(scope: CoroutineScope, msg: String) {
        user?.apply {
            val message = buildTeleMessage(EVENT_TYPE_MESSAGE, msg, this)
            sendMessage(scope, message)
        }
    }

    fun sendNotifyMessage(scope: CoroutineScope, msg: String) {
        user?.apply {
            val message = buildTeleMessage(EVENT_TYPE_NOTIFY, msg, this)
            sendMessage(scope, message)
        }
    }

    fun sendGameMessage(scope: CoroutineScope, msg: String) {
        user?.apply {
            val message = buildTeleMessage(EVENT_TYPE_GAME, msg, this)
            sendMessage(scope, message)
        }
    }

    fun sendAckMessage(
        scope: CoroutineScope,
        msg: String,
        onReceiveMessage: suspend (String) -> Boolean
    ) {
        sendDefaultMessage(scope, msg)
        openMessageReceiver(scope, onReceiveMessage)
    }

    private fun openMessageReceiver(
        scope: CoroutineScope,
        onReceiveMessage: suspend (String) -> Boolean
    ) {
        scope.launch {
            kotlin.runCatching {
                while(true) {
                    val line = socketReader?.readLine()
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
            }.onFailure {
                Log.d(TAG, "openMessageReceiver error: ${it.message}")
                reConnect()
                openMessageReceiver(scope, onReceiveMessage)
            }
        }
    }

    private fun buildTeleMessage(type: Int, msg: String, user: UserBean): TelecomBean {
        return TelecomBean(
            type,
            content = msg,
            userId = user.userId,
            nickName = user.nickname,
            session = user.session
        )
    }

    fun setOnlineSocket(socket: Socket) {
        onlineSocket = socket
        if(socketReader == null) {
            socketReader = BufferedReader(InputStreamReader(onlineSocket.getInputStream(), "UTF-8"))
        }
        if(socketWriter == null) {
            socketWriter = BufferedWriter(OutputStreamWriter(onlineSocket.getOutputStream(), "UTF-8"))
        }
    }

    fun destroySocket() {
        if(onlineSocket.isClosed.not()) {
            onlineSocket.close()
        }
    }

    private fun reConnect(msg: TelecomBean? = null) {
        val future = CompletableFuture<Unit>()
        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                socketReader = null
                socketWriter = null
                onlineSocket = Socket(baseSocketPath, socketPort)
                socketReader = BufferedReader(InputStreamReader(onlineSocket.getInputStream(), "UTF-8"))
                socketWriter = BufferedWriter(OutputStreamWriter(onlineSocket.getOutputStream(), "UTF-8"))
                Log.d(TAG,"connection OK!")
                val mUserid = user?.userId
                if (mUserid != null) {
                    sendDefaultMessage(scope = CoroutineScope(Dispatchers.IO), mUserid)
                }
                msg?.let {
                    socketWriter?.write("$msg\n")
                    socketWriter?.flush()
                }
                future.complete(Unit)
            }.onFailure {
                Log.d(TAG,"reConnect error,try again!")
                future.complete(Unit)
                reConnect()
            }
        }
        future.get()
    }
}