package com.vdzon.irrigation.components.hardware.api

import com.vdzon.irrigation.model.IrrigationArea

interface Hardware {
    fun start()
    fun setPump(on: Boolean)
    fun setArea(area: IrrigationArea)
    fun setLedState(led: Led, on: Boolean)
    fun displayLine(lineNr: Int, line: String)
    fun registerSwitchListener(switchListener: ButtonListener)
}

