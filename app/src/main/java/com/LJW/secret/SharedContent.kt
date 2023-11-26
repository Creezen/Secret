package com.ljw.secret

import com.ljw.secret.POJO.User
import okhttp3.Cookie
import java.lang.NullPointerException
import java.net.Socket

const val LOCAL_SOCKET_PORT = 10156
const val BASE_URL = "https://2xrnknq21l5m.hk1.xiaomiqiu123.top"
const val BASE_SOCKET_PATH = "hk1.xiaomiqiu123.top"
const val BASIC_LETTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

lateinit var OnlineUser: User

var UserSocket : Socket? = null
    set(value) {
        if(field == null) field = value
    }
    get() = field?:throw NullPointerException("错误，账号未成功登陆时，socket为空！！")

val COOKIE_LIST:MutableList<Cookie> = mutableListOf()

val COOKIE_MAP:MutableMap<String,Long> = mutableMapOf()