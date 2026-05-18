package com.jayce.vexis.foundation.ability.socket

import com.jayce.vexis.foundation.ability.socket.LanManager.Companion.TYPE_CLIENT
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class LanClient(uri: URI, private val listener: LanConnectionListener) : WebSocketClient(uri) {

    override fun onOpen(handshakedata: ServerHandshake?) {
        listener.onOpen(TYPE_CLIENT, null, null,  handshakedata)
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        listener.onClose(TYPE_CLIENT, null, code, reason, remote)
    }

    override fun onMessage(message: String?) {
        if (message.isNullOrEmpty()) return
        listener.handleMessage(TYPE_CLIENT, null, message)
    }

    override fun onError(ex: Exception?) {
        listener.onError(TYPE_CLIENT,  null, ex)
    }
}