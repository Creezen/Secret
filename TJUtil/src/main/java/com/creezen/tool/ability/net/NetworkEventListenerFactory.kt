package com.creezen.tool.ability.net

import okhttp3.Call
import okhttp3.EventListener

class NetworkEventListenerFactory : EventListener.Factory {

    override fun create(call: Call) = NetworkEventListener()
}