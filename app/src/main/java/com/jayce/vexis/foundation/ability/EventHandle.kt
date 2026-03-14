package com.jayce.vexis.foundation.ability

import android.util.Log
import com.creezen.commontool.toBean
import com.creezen.tool.NetTool
import com.creezen.tool.NetTool.registerSocket
import com.creezen.tool.ThreadTool
import com.jayce.vexis.core.SessionManager.BASE_SOCKET_PATH
import com.jayce.vexis.core.SessionManager.LOCAL_SOCKET_PORT
import com.jayce.vexis.core.SessionManager.liveUser
import com.jayce.vexis.domain.EventRepository
import com.jayce.vexis.domain.bean.EventEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.net.Socket

object EventHandle : KoinComponent {

    const val NAME_MESSAGE_SCOPE = "MESSAGE"

    private val repository by inject<EventRepository>()

    fun initEventSystem() {
        val mScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        ThreadTool.registerScope(NAME_MESSAGE_SCOPE, mScope)
        ThreadTool.runOnSpecific(NAME_MESSAGE_SCOPE) {
            val socket = Socket(BASE_SOCKET_PATH, LOCAL_SOCKET_PORT)
            registerSocket(socket, true)
            NetTool.connect(mScope, liveUser.userId) {
                val message = it.toBean<EventEntry>() ?: return@connect true
                repository.insertEvent(message)
                return@connect true
            }
        }.onFailure {
            Log.d("LJW", "runOnSpecific error: ${it.message}")
        }
    }

    fun releaseEventSystem() {
        ThreadTool.unregisterScope(NAME_MESSAGE_SCOPE)
    }
}