package com.jayce.vexis.foundation.ability

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.creezen.tool.TLog
import com.jayce.vexis.StatusManager
import com.jayce.vexis.StatusManager.NETWORK_TYPE_CELLULAR
import com.jayce.vexis.StatusManager.NETWORK_TYPE_INVALIDATED
import com.jayce.vexis.StatusManager.NETWORK_TYPE_LOST
import com.jayce.vexis.StatusManager.NETWORK_TYPE_UNKNOWN
import com.jayce.vexis.StatusManager.NETWORK_TYPE_WIFI

class NetStatusCallback : ConnectivityManager.NetworkCallback() {

    override fun onCapabilitiesChanged(
        network: Network,
        networkCapabilities: NetworkCapabilities
    ) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        if (!networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            StatusManager.networkType = NETWORK_TYPE_INVALIDATED
            return
        }
        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            StatusManager.networkType = NETWORK_TYPE_CELLULAR
        } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            StatusManager.networkType = NETWORK_TYPE_WIFI
        } else {
            StatusManager.networkType = NETWORK_TYPE_UNKNOWN
        }
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        StatusManager.networkType = NETWORK_TYPE_LOST
    }
}