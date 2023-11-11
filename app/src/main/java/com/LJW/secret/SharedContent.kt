package com.LJW.secret

import com.LJW.secret.POJO.User
import okhttp3.Cookie

lateinit var OnlineUser: User

val COOKIE_LIST:MutableList<Cookie> = mutableListOf()

val COOKIE_MAP:MutableMap<String,Long> = mutableMapOf()