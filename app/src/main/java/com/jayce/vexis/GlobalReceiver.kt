package com.jayce.vexis

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.creezen.tool.Constant.BROADCAST_LOGOUT

class GlobalReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) {
            return
        }
        when(intent.action) {
            BROADCAST_LOGOUT -> Log.e("GlobalReceiver.onReceive","登出")
        }
    }
}