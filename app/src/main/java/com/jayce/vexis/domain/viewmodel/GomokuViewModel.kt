package com.jayce.vexis.domain.viewmodel

import android.content.Context
import android.util.Log
import com.creezen.tool.NetTool
import com.creezen.tool.ThreadTool
import com.jayce.vexis.core.base.BaseViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.java_websocket.WebSocket
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.handshake.ServerHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.net.URI
import java.util.concurrent.TimeUnit

class GomokuViewModel : BaseViewModel() {

    companion object {
        private const val PORT: Int = 8887

        const val NO_JUMP = 0
        const val FIRST_TO_SECOND = 1
        const val FIRST_TO_THIRD = 2
        const val SECOND_BACK_FIRST = 3
        const val THIRD_BACK_FIRST = 4
    }

    var isServer: Boolean = false
    private var stage: Int = NO_JUMP
    var isServerChecked: Boolean = true
    var ipInput: String = ""
    var isWifiEnabled: Boolean = false
    private var inConnecting: Boolean = false
    private var isClientConnected: Boolean = false
    private var clientList: ArrayList<WebSocket> = arrayListOf()

    private val _buttonTextFlow: MutableSharedFlow<Pair<String, String>>
        = MutableSharedFlow(0, 5, BufferOverflow.SUSPEND)
    val buttonTextFlow = _buttonTextFlow.asSharedFlow()

    private val _toastFlow: MutableSharedFlow<String>
            = MutableSharedFlow(0, 5, BufferOverflow.SUSPEND)
    val toastFlow = _toastFlow.asSharedFlow()

    private val _pageFlow: MutableSharedFlow<Int>
            = MutableSharedFlow(0, 5, BufferOverflow.SUSPEND)
    val pageFlow = _pageFlow.asSharedFlow()

    private val _dialogFlow: MutableSharedFlow<Boolean>
            = MutableSharedFlow(0, 5, BufferOverflow.SUSPEND)
    val dialogFlow = _dialogFlow.asSharedFlow()

    private val _chessFlow: MutableSharedFlow<Triple<Int, Int, Boolean>>
            = MutableSharedFlow(0, 5, BufferOverflow.SUSPEND)
    val chessFlow = _chessFlow.asSharedFlow()

    private var clientSocket: WebSocketClient? = null

    private var serverSocket: WebSocketServer? = null

    private fun launch() {
        serverSocket?.stop()
        serverSocket = object : WebSocketServer(InetSocketAddress(PORT)) {
            override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
                if (conn == null) return
                isServer = true
                ThreadTool.runOnMulti { _dialogFlow.emit(true) }
                clientList.add(conn)
                Log.d("LJW", "onOpen1:  ${handshake?.resourceDescriptor}")
            }

            override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
                Log.d("LJW", "onClose1:  $code  $reason  $remote")
            }

            override fun onMessage(conn: WebSocket?, message: String?) {
                if (message == null) return
                Log.d("LJW", "message1: $message")
                ThreadTool.runOnMulti {  _chessFlow.emit(parsePosition(message)) }
                clientList.forEach { it.send(message) }
            }

            override fun onError(conn: WebSocket?, ex: Exception?) {
                Log.d("LJW", "onError1: ${ex?.message}")
            }

            override fun onStart() {
                Log.d("LJW", "onStart1")
            }
        }
        serverSocket?.start()
    }

    private fun tryConnect(ip: String) {
        if (inConnecting) {
            Log.e("LJW", "正在连接中...")
            return
        }
        inConnecting = true
        var time = 5
        ThreadTool.runOnMulti {
            while (time-- > 0) {
                if (!inConnecting) break
                if (isClientConnected) return@runOnMulti
                connect(ip)
            }

            val toastText: String
            if (time >= 0) {
                toastText = "已取消连接"
            } else {
                handleThirdJumpFirst(false)
                toastText = "连接失败，请检查IP是否正确"
            }
            _toastFlow.emit(toastText)
        }
    }

    private fun connect(ip: String) {
        clientSocket?.close()
        val uri = URI("ws://${ip}:$PORT")
        clientSocket = object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                isServer = false
                ThreadTool.runOnMulti { _dialogFlow.emit(false) }
                Log.d("LJW", "onOpen2: ${handshakedata?.httpStatusMessage}")
            }

            override fun onMessage(message: String?) {
                if (message == null) return
                ThreadTool.runOnMulti {  _chessFlow.emit(parsePosition(message)) }
                Log.d("LJW", "message2: $message")
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d("LJW", "onClose2: $code  $reason  $remote")
            }

            override fun onError(ex: Exception?) {
                Log.d("LJW", "onError2: ${ex?.message}")
            }
        }
        isClientConnected = clientSocket?.connectBlocking(5, TimeUnit.SECONDS) ?: false
    }

    private fun getAndUpdateStage() {
        stage = when (stage) {
            FIRST_TO_SECOND -> SECOND_BACK_FIRST
            FIRST_TO_THIRD -> THIRD_BACK_FIRST
            NO_JUMP, SECOND_BACK_FIRST, THIRD_BACK_FIRST -> {
                if (isServerChecked) FIRST_TO_SECOND else FIRST_TO_THIRD
            }
            else -> NO_JUMP
        }
    }

    suspend fun updateButtonStatus() {
        getAndUpdateStage()
        when (stage) {
            FIRST_TO_SECOND -> handleFirstJumpSecond()
            SECOND_BACK_FIRST -> handleSecondJumpFirst()
            FIRST_TO_THIRD -> handleFirstJumpThird()
            THIRD_BACK_FIRST -> handleThirdJumpFirst(true)
        }
    }

    private suspend fun handleFirstJumpSecond() {
        updateButtonText("", "取消匹配")
        _pageFlow.emit(FIRST_TO_SECOND)
        launch()
    }

    private suspend fun handleSecondJumpFirst() {
        updateButtonText("取消", "开启监听")
        _pageFlow.emit(SECOND_BACK_FIRST)
    }

    private suspend fun handleFirstJumpThird() {
        if (!isWifiEnabled) {
            _toastFlow.emit("请先开启WLAN")
            return
        }
        if (!isValidIP(ipInput)) {
            _toastFlow.emit("IP: [$ipInput] 不合法")
            return
        }
        updateButtonText("", "取消连接")
        _pageFlow.emit(FIRST_TO_THIRD)
        tryConnect(ipInput)
    }

    private suspend fun handleThirdJumpFirst(isManualClose: Boolean) {
        inConnecting = false
        if (isManualClose) {
            clientSocket?.closeConnection(-1, "用户手动关闭")
        }
        updateButtonText("取消", "立即连接")
        _pageFlow.emit(THIRD_BACK_FIRST)
    }

    private suspend fun updateButtonText(cancelText: String, confirmText: String) {
        _buttonTextFlow.emit(cancelText to confirmText)
    }

    fun updateButtonText(pair: Pair<String, String>) {
        ThreadTool.runOnMulti {
            _buttonTextFlow.emit(pair)
        }
    }

    fun shouldCheckWifiStatus(): Boolean {
        val isFirstPage = stage == NO_JUMP
                          || stage == SECOND_BACK_FIRST
                          || stage == THIRD_BACK_FIRST
        return !isServerChecked && isFirstPage
    }

    fun getIPInfo(context: Context): String {
        val ipList = NetTool.getDeviceIP(context)
        var info = "本机IP信息如下所示:\n"
        ipList.forEach {
            info += "\n\t$it"
        }
        return info
    }

    private fun isValidIP(ip: String): Boolean {
        val regex = Regex("^(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\$")
        val ipList = ip.split(".")
        if (ipList.size != 4) return false
        ipList.forEach {
            if (!regex.matches(it)) return false
        }
        return true
    }

    fun formatElapsed(millis: Long): String {
        val ms = millis % 1000
        val totalSeconds = millis / 1000
        val sec = totalSeconds % 60
        val min = (totalSeconds / 60) % 60
        val hour = totalSeconds / 3600
        return "%02d:%02d:%02d.%03d".format(hour, min, sec, ms)
    }

    private fun parsePosition(str: String): Triple<Int, Int, Boolean> {
        val positionChars = str.split(":").map { it.toInt() }
        val isBlackPlayer = positionChars[2] == 1
        return Triple(positionChars[0], positionChars[1], isBlackPlayer)
    }

    fun placeChess(x: Int, y: Int, isBlackPlayer: Boolean) {
        val flag = if (isBlackPlayer) 1 else 0
        val text = "$x:$y:$flag"
        if (!isServer) {
            clientSocket?.send(text)
        } else {
            clientList.forEach { it.send(text) }
        }
    }
}