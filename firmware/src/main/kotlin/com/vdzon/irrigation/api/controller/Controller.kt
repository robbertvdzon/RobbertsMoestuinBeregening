package com.vdzon.irrigation.api.controller

import com.vdzon.irrigation.api.commandprocessor.CommandProcessorListener
import com.vdzon.irrigation.api.hardware.ButtonListener
import com.vdzon.irrigation.api.network.NetworkListener

interface Controller: ButtonListener, CommandProcessorListener, NetworkListener {
    fun start()
}