package com.jayce.vexis.business.kit.gomoku

import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import com.creezen.tool.AndroidTool.toast
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityGomokuBinding
import org.java_websocket.WebSocket
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.handshake.ServerHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.net.URI

class GomokuActivity : BaseActivity<ActivityGomokuBinding>() {

    private var serverSocket: WebSocketServer? = null
    private var clientSocket: WebSocketClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPage()
    }

    private fun initPage() {
        binding.server.setOnClickListener {
            launch()
        }
        binding.client.setOnClickListener {
            val manager = getSystemService(WifiManager::class.java)
            if (!manager.isWifiEnabled) {
                "请先开启WLAN".toast()
            }
            connect()
        }
        binding.message.setOnClickListener {
            clientSocket?.send("hello world")
        }
    }

    private fun launch() {
        serverSocket = object : WebSocketServer(InetSocketAddress(8887)) {
            override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
                Log.d("LJW", "onOpen1")
            }

            override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
                Log.d("LJW", "onClose1")
            }

            override fun onMessage(conn: WebSocket?, message: String?) {
                conn?.send("receive OK")
                Log.d("LJW", "message1: $message")
            }

            override fun onError(conn: WebSocket?, ex: Exception?) {
                Log.d("LJW", "onError1")
            }

            override fun onStart() {
                Log.d("LJW", "onStart1")
            }
        }
        serverSocket?.start()
    }

    private fun connect() {
        val uri = URI("ws://192.168.3.14:8887")
        clientSocket = object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d("LJW", "onOpen2")
            }

            override fun onMessage(message: String?) {
                Log.d("LJW", "message2: $message")
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d("LJW", "onClose2")
            }

            override fun onError(ex: Exception?) {
                Log.d("LJW", "onError2")
            }
        }
        clientSocket?.connect()
    }
}