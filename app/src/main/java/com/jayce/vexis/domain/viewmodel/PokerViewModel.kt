package com.jayce.vexis.domain.viewmodel

import com.jayce.vexis.client.AndroidTool.toast
import com.jayce.vexis.client.TLog
import com.jayce.vexis.core.base.BaseViewModel
import com.jayce.vexis.foundation.ability.socket.LanConnectionListener
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception

class PokerViewModel : BaseViewModel(), LanConnectionListener {

    fun request() {
        "aaa".toast()
    }

    override fun onServerStart() {
        TLog.d("onServerStart")
    }

    override fun onOpen(
        type: Int,
        conn: WebSocket?,
        handshake: ClientHandshake?,
        handshakedata: ServerHandshake?
    ) {}

    override fun onClose(type: Int, conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {}

    override fun handleMessage(type: Int, conn: WebSocket?, message: String): String {
        return message
    }

    override fun onError(type: Int, conn: WebSocket?, ex: Exception?) {}

    override fun onStageChange(stage: Int) {}

    override fun onCancel() {/**/ }
}