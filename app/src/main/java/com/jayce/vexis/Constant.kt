package com.jayce.vexis

object Constant {
    val LOCAL_SOCKET_PORT = BuildConfig.socketPort
    val BASE_SOCKET_PATH = BuildConfig.socketUrl
    const val API_BASE_URL = "https://www.mxnzp.com/api/convert/"
    const val BASE_FILE_PATH = "${BuildConfig.baseUrl}/FileSystem/"
    const val DICTIONARY_API_APPID = "ckp9k9hlvylykswh"
    const val DICTIONARY_API_APPSECRET = "f1pJSDJ6qIDZGPeL5Q1EUqkZXfHgQ9qA"
    const val PARAGRAPH_HEAD = "\t\t\t\t"
    const val BROAD_LOGOUT = "com.jayce.vexis.logout.broadcast"
    const val BROAD_NOTIFY = "com.jayce.vexis.notify.broadcast"
}