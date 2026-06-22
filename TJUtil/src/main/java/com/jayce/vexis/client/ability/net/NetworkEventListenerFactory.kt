package com.jayce.vexis.client.ability.net

import okhttp3.Call
import okhttp3.EventListener

class NetworkEventListenerFactory(private val debug: Boolean) : EventListener.Factory {

    override fun create(call: Call) = NetworkEventListener(debug)
}