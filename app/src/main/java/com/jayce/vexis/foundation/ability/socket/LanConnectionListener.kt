package com.jayce.vexis.foundation.ability.socket

import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception

interface LanConnectionListener {

    fun onServerStart()

    fun onOpen(type: Int, conn: WebSocket?, handshake: ClientHandshake?, handshakedata: ServerHandshake?)

    fun onClose(type: Int, conn: WebSocket?, code: Int, reason: String?, remote: Boolean)

    fun handleMessage(type: Int, conn: WebSocket?, message: String): String

    fun onError(type: Int, conn: WebSocket?, ex: Exception?)

    fun onStageChange(stage: Int)

    fun onCancel()
}