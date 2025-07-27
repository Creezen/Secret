package com.jayce.vexis.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.AndroidTool.writePrefs
import com.creezen.tool.DataTool.toJson
import com.jayce.vexis.core.Config.BROAD_LOGOUT
import com.jayce.vexis.core.Config.BROAD_NOTIFY

class CoreReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "CoreReceiver"
    }

    override fun onReceive(
        context: Context?,
        intent: Intent?,
    ) {
        if (intent == null) return
        when (intent.action) {
            BROAD_LOGOUT -> "logout".toast()
            BROAD_NOTIFY -> {
                val msg = intent.getStringExtra("broadcastNotify")
                msg?.toast()
            }
            Intent.ACTION_CLOSE_SYSTEM_DIALOGS -> {
                Log.d(TAG, "write message")
                writePrefs {
                    CoreService.getBackupContent().toJson()?.let { str ->
                        it.putString(CoreService.CACHE_MESSAGE, str)
                    }
                }
            }
        }
    }
}