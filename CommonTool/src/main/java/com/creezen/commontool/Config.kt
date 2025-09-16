package com.creezen.commontool

object Config {

    const val API_BASE_URL = "https://www.mxnzp.com/api/convert/"
    const val DICTIONARY_API_APPID = "ckp9k9hlvylykswh"
    const val DICTIONARY_API_APPSECRET = "f1pJSDJ6qIDZGPeL5Q1EUqkZXfHgQ9qA"
    const val BROAD_LOGOUT = "com.jayce.vexis.logout.broadcast"
    const val BROAD_NOTIFY = "com.jayce.vexis.notify.broadcast"

    object Constant {
        const val EMPTY_STRING = ""
        const val BASIC_LETTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    }

    object EventType {
        const val EVENT_TYPE_DEFAULT = 0
        const val EVENT_TYPE_MESSAGE = 1
        const val EVENT_TYPE_NOTIFY = 2
        const val EVENT_TYPE_GAME = 3
    }

}