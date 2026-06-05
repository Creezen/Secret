package com.jayce.vexis.domain.viewmodel

import com.creezen.tool.TLog
import com.creezen.tool.ThreadTool.runOnMulti
import com.jayce.vexis.core.base.BaseViewModel
import com.jayce.vexis.domain.bean.ChessEntry
import com.jayce.vexis.foundation.ability.socket.LanConnectionListener
import com.jayce.vexis.foundation.ability.socket.LanManager.Companion.TYPE_CLIENT
import com.jayce.vexis.foundation.ability.socket.LanManager.Companion.TYPE_SERVER
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.handshake.ServerHandshake

class GomokuViewModel : BaseViewModel(), LanConnectionListener {

    var isServer: Boolean = false

    private val _dialogFlow: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val dialogFlow = _dialogFlow.asSharedFlow()

    private val _chessFlow: MutableSharedFlow<ChessEntry> = MutableSharedFlow(0, 5)
    val chessFlow = _chessFlow.asSharedFlow()

    private fun parsePosition(str: String): Triple<Int, Int, Boolean> {
        val positionChars = str.split(":").map { it.toInt() }
        val isBlackPlayer = positionChars[2] == TYPE_SERVER
        return Triple(positionChars[0], positionChars[1], isBlackPlayer)
    }

    fun sendChessLocation(x: Int, y: Int) {
        val type = if (isServer) TYPE_SERVER else TYPE_CLIENT
        runOnMulti {
            val entry = ChessEntry(true, type, x, y)
            _chessFlow.emit(entry)
        }
    }

    override fun onServerStart() {
        TLog.d("onServerStart")
    }

    override fun onOpen(
        type: Int,
        conn: WebSocket?,
        handshake: ClientHandshake?,
        handshakedata: ServerHandshake?
    ) {
        val isFromServer = type == TYPE_SERVER
        isServer = isFromServer
        runOnMulti { _dialogFlow.emit(false) }
    }

    override fun onClose(type: Int, conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        TLog.d("type: $type onClose")
    }

    override fun handleMessage(type: Int, conn: WebSocket?, message: String): String {
        runOnMulti {
           val triple = parsePosition(message)
           val sendType = if (triple.third) TYPE_SERVER else TYPE_CLIENT
           val chessEntry = ChessEntry(
               false,
               sendType,
               triple.first,
               triple.second
           )
           _chessFlow.emit(chessEntry)
       }
        return message
    }

    override fun onError(type: Int, conn: WebSocket?, ex: java.lang.Exception?) {
        TLog.d("type: $type onError")
    }

    override fun onCancel() {
        runOnMulti { _dialogFlow.emit(true) }
    }

    override fun onStageChange(stage: Int) {
        TLog.d("onStageChange: $stage")
    }
}