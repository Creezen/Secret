package com.jayce.vexis.foundation.ability.socket

import android.content.Context
import com.jayce.vexis.client.NetTool
import com.jayce.vexis.client.NetTool.isValidIP
import com.jayce.vexis.client.TLog
import com.jayce.vexis.client.ThreadTool
import java.net.URI
import java.util.concurrent.TimeUnit

class LanManager {

    companion object {
        private const val PORT: Int = 8887

        const val TYPE_CLIENT = 1
        const val TYPE_SERVER = -1

        const val NO_JUMP = 0
        const val FIRST_TO_SECOND = 1
        const val FIRST_TO_THIRD = 2
        const val SECOND_BACK_FIRST = 3
        const val THIRD_BACK_FIRST = 4
    }

    var isWifiEnabled: Boolean = false
    var isServerChecked: Boolean = true
    var ipInput: String = ""
    private var stage: Int = NO_JUMP
    private var inConnecting: Boolean = false
    private var isClientConnected: Boolean = false

    private var clientSocket: LanClient? = null
    private var serverSocket: LanServer? = null

    private var innerLanStatusListener: InnerLanStatusListener? = null
    private var lanConnectionListener: LanConnectionListener? = null

    fun addInnerListener(inner: InnerLanStatusListener, connection: LanConnectionListener) {
        innerLanStatusListener = inner
        lanConnectionListener = connection
    }

    private fun launch() {
        serverSocket?.stop()
        lanConnectionListener?.let {
            serverSocket = LanServer(PORT, it)
            serverSocket?.start()
        }
    }

    private fun connect(ip: String) {
        clientSocket?.close()
        val uri = URI("ws://$ip:$PORT")
        lanConnectionListener?.let {
            clientSocket = LanClient(uri, it)
            isClientConnected = clientSocket?.connectBlocking(5, TimeUnit.SECONDS) ?: false
        }
    }

    private fun tryConnect(ip: String, tryTime: Int = 5) {
        if (inConnecting) {
            TLog.e("正在连接中...")
            return
        }
        inConnecting = true
        var time = tryTime
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
            innerLanStatusListener?.onToast(toastText)
        }
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

    fun updateButtonStatus() {
        getAndUpdateStage()
        when (stage) {
            FIRST_TO_SECOND -> handleFirstJumpSecond()
            SECOND_BACK_FIRST -> handleSecondJumpFirst()
            FIRST_TO_THIRD -> handleFirstJumpThird()
            THIRD_BACK_FIRST -> handleThirdJumpFirst(true)
        }
    }

    private fun handleFirstJumpSecond() {
        updateButtonText("", "取消匹配")
        innerLanStatusListener?.onStageChange(FIRST_TO_SECOND)
        launch()
    }

    private fun handleSecondJumpFirst() {
        updateButtonText("取消", "开启监听")
        innerLanStatusListener?.onStageChange(SECOND_BACK_FIRST)
    }

    private fun handleFirstJumpThird() {
        if (!isWifiEnabled) {
            innerLanStatusListener?.onToast("请先开启WLAN")
            return
        }
        if (!isValidIP(ipInput)) {
            innerLanStatusListener?.onToast("IP: [$ipInput] 不合法")
            return
        }
        updateButtonText("", "取消连接")
        innerLanStatusListener?.onStageChange(FIRST_TO_THIRD)
        tryConnect(ipInput)
    }

    private fun handleThirdJumpFirst(isManualClose: Boolean) {
        inConnecting = false
        if (isManualClose) {
            clientSocket?.closeConnection(-1, "用户手动关闭")
        }
        updateButtonText("取消", "立即连接")
        innerLanStatusListener?.onStageChange(THIRD_BACK_FIRST)
    }

    fun updateButtonText(cancelText: String, confirmText: String) {
        innerLanStatusListener?.onActionChange(cancelText, confirmText)
    }

    fun sendMessage(type: Int, message: String) {
        when (type) {
            TYPE_CLIENT -> clientSocket?.send(message)
            TYPE_SERVER -> serverSocket?.sendMessage(message)
        }
    }

    fun shouldCheckWifiStatus(): Boolean {
        val isFirstPage = stage == NO_JUMP ||
                stage == SECOND_BACK_FIRST ||
                stage == THIRD_BACK_FIRST
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
}