package com.vdzon.irrigation.components.network.api

interface Network {
    fun start()
    fun registerNetworkListener(networkListener: NetworkListener)

}