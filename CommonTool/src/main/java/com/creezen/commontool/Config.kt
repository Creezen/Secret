package com.creezen.commontool

object Config {

    const val API_BASE_URL = "https://www.mxnzp.com/api/convert/"
    const val DICTIONARY_API_APPID = "ckp9k9hlvylykswh"
    const val DICTIONARY_API_APPSECRET = "f1pJSDJ6qIDZGPeL5Q1EUqkZXfHgQ9qA"
    const val BROAD_LOGOUT = "com.jayce.vexis.logout.broadcast"
    const val BROAD_NOTIFY = "com.jayce.vexis.notify.broadcast"

    object Constant {
        const val NUM_0 = 0
        const val NUM_1 = 1
        const val NUM_2 = 2
        const val NUM_3 = 3
        const val NUM_4 = 4
        const val NUM_5 = 5
        const val NUM_6 = 6
        const val NUM_7 = 7
        const val NUM_8 = 8
        const val NUM_9 = 9
        const val LONG_0 = 0L
        const val LONG_1 = 1L
        const val LONG_2 = 2L
        const val LONG_3 = 3L
        const val LONG_4 = 4L
        const val LONG_5 = 5L
        const val LONG_6 = 6L
        const val LONG_7 = 7L
        const val LONG_8 = 8L
        const val LONG_9 = 9L
        const val EMPTY_STRING = ""
        const val BASIC_LETTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    }

    object EventType {
        const val EVENT_TYPE_DEFAULT = 0
        const val EVENT_TYPE_MESSAGE = 1
        const val EVENT_TYPE_NOTIFY = 2
        const val EVENT_TYPE_GAME = 3
    }

    object FragmentTag {
        const val FRAGMENT_FEEDBACK = "feedback"
        const val FRAGMENT_KIT = "kitFragment"
        const val FRAGMENT_HISTORY = "historicalAxis"
        const val FRAGMENT_ARTICLE = "articleFragment"
        const val FRAGMENT_SENIOR = "senior"
        const val FRAGMENT_FILE = "mediaLibrarFragment"
        const val FRAGMENT_MAP = "mapFragment"
    }

    object PreferenceParam {
        const val AVATAR_SAVE_TIME = "cursorTime"
    }

    object MediaTypeParam {
        const val MEDIA_TYPE_ALL = "*/*"
        const val MEDIA_TYPE_IMAGE = "image/*"
    }

    object QRCodeParam {
        const val URL_PREFIX = "https://com.jayce.vexis"
    }

}