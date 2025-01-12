package com.vdzon.irrigation.components.controller

import com.vdzon.irrigation.components.commandprocessor.api.CommandProcessorListener
import com.vdzon.irrigation.components.hardware.api.ButtonListener
import com.vdzon.irrigation.components.network.api.NetworkListener

interface Controller: ButtonListener, CommandProcessorListener, NetworkListener {
    fun start()
}