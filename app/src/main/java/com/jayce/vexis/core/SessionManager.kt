package com.jayce.vexis.core

import com.jayce.vexis.BuildConfig
import com.jayce.vexis.foundation.bean.UserEntry
import java.util.concurrent.atomic.AtomicReference

object SessionManager {

    val LOCAL_SOCKET_PORT = BuildConfig.socketPort
    val BASE_SOCKET_PATH = BuildConfig.socketUrl
    const val BASE_FILE_PATH = "${BuildConfig.baseUrl}/FileSystem/"

    private var onlineUserEntry = AtomicReference(UserEntry())

    fun registerUser(userEntry: UserEntry) {
        onlineUserEntry.set(userEntry)
    }

    fun user() = onlineUserEntry.get()
}