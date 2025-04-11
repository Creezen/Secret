package com.jayce.vexis

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.AndroidTool.writePrefs
import com.creezen.tool.Constant.BROAD_LOGOUT
import com.creezen.tool.Constant.BROAD_NOTIFY
import com.creezen.tool.DataTool.toJson

class GlobalReceiver : BroadcastReceiver() {
    companion object {
        const val TAG = "GlobalReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null)  return
        when(intent.action) {
            BROAD_LOGOUT -> "logout".toast()
            BROAD_NOTIFY -> {
                val msg = intent.getStringExtra("broadcastNotify")
                msg?.toast()
            }
            Intent.ACTION_CLOSE_SYSTEM_DIALOGS -> {
                Log.d(TAG,"write message")
                writePrefs {
                    CreezenService.getBackupContent().toJson()?.let { str ->
                        it.putString(CreezenService.CACHE_MESSAGE, str)
                    }
                }
            }
        }
    }
}
