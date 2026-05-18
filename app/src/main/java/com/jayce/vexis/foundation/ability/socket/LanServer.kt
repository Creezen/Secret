package com.jayce.vexis.foundation.ability.socket

import com.jayce.vexis.foundation.ability.socket.LanManager.Companion.TYPE_SERVER
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress

class LanServer(port: Int, private val listener: LanConnectionListener) : WebSocketServer(InetSocketAddress(port)) {

    private var clientList: ArrayList<WebSocket> = arrayListOf()

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        if (conn == null) return
        clientList.add(conn)
        listener.onOpen(TYPE_SERVER, conn, handshake, null)
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        if (conn == null) return
        listener.onClose(TYPE_SERVER, conn, code, reason, remote)
    }

    override fun onMessage(conn: WebSocket?, message: String?) {
        if (conn == null) return
        if (message.isNullOrEmpty()) return
        val handleMessage = listener.handleMessage(TYPE_SERVER, conn, message)
        clientList.forEach { it.send(handleMessage) }
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        if (conn == null) return
        listener.onError(TYPE_SERVER,  conn, ex)
    }

    override fun onStart() {
        listener.onServerStart()
    }

    fun sendMessage(message: String) {
        clientList.forEach { it.send(message) }
    }
}