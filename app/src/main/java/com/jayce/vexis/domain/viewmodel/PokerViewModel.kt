package com.jayce.vexis.domain.viewmodel

import android.util.Log
import com.creezen.tool.AndroidTool.toast
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
        Log.d("LJW", "onServerStart")
    }

    override fun onOpen(
        type: Int,
        conn: WebSocket?,
        handshake: ClientHandshake?,
        handshakedata: ServerHandshake?
    ) {
        Log.d("LJW", "")
    }

    override fun onClose(type: Int, conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        Log.d("LJW", "")
    }

    override fun handleMessage(type: Int, conn: WebSocket?, message: String): String {
        Log.d("LJW", "")
        return message
    }

    override fun onError(type: Int, conn: WebSocket?, ex: Exception?) {
        Log.d("LJW", "")
    }

    override fun onStageChange(stage: Int) {
        Log.d("LJW", "")
    }

    override fun onCancel() {
        Log.d("LJW", "")
    }
}