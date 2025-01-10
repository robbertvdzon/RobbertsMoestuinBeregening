package com.vdzon.irrigation.components.hardware.api

import com.vdzon.irrigation.components.controller.KlepState
import com.vdzon.irrigation.components.hardware.impl.DisplayData

interface Hardware {
    fun start()

    fun klepOpen()
    fun klepClose()

    fun updateTime(time: String)
    fun updateIP(ip: String)
    fun updateKlepState(klepState: KlepState)

    fun encoderUp()
    fun encoderDown()
    fun registerEncoderListener(encoderListener: EncoderListener)
    fun registerSwitchListener(switchListener: SwitchListener)
    fun registerKlepListener(klepListener: KlepListener)
    fun getDisplayData(): DisplayData
}

interface SwitchListener{
//    fun switchOn()
//    fun switchOff()
}

interface EncoderListener{
    fun encoderUp(amount: Int = 1)
    fun encoderDown(amount: Int = 1)
    fun startUpdating()
    fun dicht()
}

interface KlepListener{
    fun klepOpen()
    fun klepClosed()
}