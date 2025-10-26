package com.jayce.vexis.foundation.ability

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.creezen.tool.AndroidTool.toast

class NetStatusCallback : ConnectivityManager.NetworkCallback() {

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        if (!networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            "当前网络无法上网".toast()
            return
        }
        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            "正在使用移动网络".toast()
        } else if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            "正在使用WIFI网络".toast()
        } else if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)) {
            "正在使用蓝牙".toast()
        } else {
            "正在使用其他网络".toast()
        }
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        "网络已断开".toast()
    }
}