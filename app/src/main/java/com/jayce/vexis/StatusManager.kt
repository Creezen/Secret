package com.jayce.vexis

import com.creezen.commontool.bean.UserBean
import com.creezen.tool.NetTool
import java.util.concurrent.atomic.AtomicReference

object StatusManager {

    val LOCAL_SOCKET_PORT = BuildConfig.socketPort
    val BASE_SOCKET_PATH = BuildConfig.socketUrl
    const val BASE_FILE_PATH = "${BuildConfig.baseUrl}/FileSystem/"

    var isLogin: Boolean = false

    private var onlineUserEntry = AtomicReference(UserBean())

    val liveUser: UserBean
        get() = onlineUserEntry.get()

    fun registerUser(userBean: UserBean) {
        onlineUserEntry.set(userBean)
        NetTool.setUser(userBean)
    }
}