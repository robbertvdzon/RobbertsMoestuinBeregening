package com.vdzon.irrigation.components.network

import com.vdzon.irrigation.api.network.Network
import com.vdzon.irrigation.api.network.NetworkListener
import java.net.NetworkInterface
import kotlin.concurrent.thread

class NetworkImpl : Network {
    private var networkListener: NetworkListener? = null
    override fun registerNetworkListener(networkListener: NetworkListener) {
        this.networkListener = networkListener
    }

    override fun start() {
        thread(start = true) {
            var currentIpAdress = getCurrentIPv4Address()
            networkListener?.setIP(currentIpAdress)
            while (true) {
                val ip = getCurrentIPv4Address()
                if (ip != currentIpAdress) {
                    networkListener?.setIP(ip)
                    currentIpAdress = ip
                }
                if (ip == "not found") {
                    Thread.sleep(5 * 1000)// check every 5 seconds when ip was not found before
                } else {
                    Thread.sleep(60 * 1000)// check every minute when ip was already found
                }
            }
        }
    }

    private fun getCurrentIPv4Address(): String {
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()
        while (networkInterfaces.hasMoreElements()) {
            val networkInterface = networkInterfaces.nextElement()
            val inetAddresses = networkInterface.inetAddresses
            while (inetAddresses.hasMoreElements()) {
                val inetAddress = inetAddresses.nextElement()
                if (!inetAddress.isLoopbackAddress && inetAddress.isSiteLocalAddress && inetAddress.hostAddress.indexOf(
                        ':'
                    ) == -1
                ) {
                    return inetAddress.hostAddress
                }
            }
        }
        return "not found"
    }


}