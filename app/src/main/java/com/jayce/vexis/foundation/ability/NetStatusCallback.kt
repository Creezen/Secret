package com.jayce.vexis.foundation.ability

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.creezen.tool.AndroidTool.toast

class NetStatusCallback(private val manager: ConnectivityManager) : ConnectivityManager.NetworkCallback() {

    private var type: Int = 0

    override fun onCapabilitiesChanged(
        network: Network,
        networkCapabilities: NetworkCapabilities
    ) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        if (!networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            "当前网络无法上网".toast()
            type = 1
            return
        }
        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            type = 2
            "当前可用移动网络".toast()
        } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            type = 3
            "当前可用WIFI网络".toast()
        } else {
            type = 4
        }
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        "网络已断开".toast()
        type = 0
    }

}