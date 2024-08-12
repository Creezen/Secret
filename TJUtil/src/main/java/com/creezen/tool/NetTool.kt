package com.creezen.tool

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Key
import com.bumptech.glide.request.RequestOptions
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.Constant.BASE_URL
import com.google.gson.GsonBuilder
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
import java.io.File
import java.util.Arrays
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object NetTool {

    private val COOKIE_LIST:MutableList<Cookie> = mutableListOf()

    private val COOKIE_MAP:MutableMap<String,Long> = mutableMapOf()

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
        return suspendCoroutine { continuation ->
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
                    Log.e(".onFailure","$p1")
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

}