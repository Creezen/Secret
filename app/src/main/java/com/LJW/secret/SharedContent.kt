package com.ljw.secret

import com.ljw.secret.bean.UserItem
import okhttp3.Cookie
import java.lang.NullPointerException
import java.net.Socket

lateinit var OnlineUserItem: UserItem

var UserSocket : Socket? = null
    set(value) {
        if(field == null) field = value
    }
    get() = field?:throw NullPointerException("错误，账号未成功登陆时，socket为空！！")

val COOKIE_LIST:MutableList<Cookie> = mutableListOf()

val COOKIE_MAP:MutableMap<String,Long> = mutableMapOf()