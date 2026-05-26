package com.creezen.commontool

object Config {

    const val ZERO = 0
    const val LONG_ZERO = 0L
    const val NIL = ""
    const val BASIC_LETTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

    const val EVENT_TYPE_DEFAULT = 0
    const val EVENT_TYPE_CHAT = 1
    const val EVENT_TYPE_FEEDBACK = 2
    const val EVENT_TYPE_EXIT = 3
    const val EVENT_TYPE_ROLE = 4

    const val ACTION_BROADCAST_LOGOUT = "com.jayce.vexis.broadcast.logout"
    const val ACTION_BROADCAST_NOTIFY = "com.jayce.vexis.broadcast.notify"

    const val FRAGMENT_FEEDBACK = "feedback"
    const val FRAGMENT_KIT = "kitFragment"
    const val FRAGMENT_HISTORY = "historicalAxis"
    const val FRAGMENT_ARTICLE = "articleFragment"
    const val FRAGMENT_SENIOR = "senior"
    const val FRAGMENT_FILE = "mediaLibrarFragment"
    const val FRAGMENT_MAP = "mapFragment"

    const val URL_PREFIX = "https://com.jayce.vexis"
    const val SERVER_DOMAIN = "xiaomiqiu.com"

    const val AVATAR_SAVE_TIME = "cursorTime"

    const val MEDIA_TYPE_ALL = "*/*"
    const val MEDIA_TYPE_IMAGE = "image/*"

    const val COOKIE_USER_ID = "userId"
    const val COOKIE_UUID = "cookId"
}