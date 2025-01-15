package com.vdzon.irrigation.api.network

interface Network {
    fun start()
    fun registerNetworkListener(networkListener: NetworkListener)

}