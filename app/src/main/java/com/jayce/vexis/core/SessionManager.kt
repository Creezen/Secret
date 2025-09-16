package com.jayce.vexis.core

import com.creezen.commontool.bean.UserBean
import com.jayce.vexis.BuildConfig
import java.util.concurrent.atomic.AtomicReference

object SessionManager {

    val LOCAL_SOCKET_PORT = BuildConfig.socketPort
    val BASE_SOCKET_PATH = BuildConfig.socketUrl
    const val BASE_FILE_PATH = "${BuildConfig.baseUrl}/FileSystem/"

    private var onlineUserEntry = AtomicReference(UserBean())

    fun registerUser(userBean: UserBean) {
        onlineUserEntry.set(userBean)
    }

    fun user() = onlineUserEntry.get()
}