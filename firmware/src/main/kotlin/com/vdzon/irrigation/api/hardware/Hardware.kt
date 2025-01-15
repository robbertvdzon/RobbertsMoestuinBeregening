package com.vdzon.irrigation.api.hardware

import com.vdzon.irrigation.api.model.IrrigationArea

interface Hardware {
    fun start()
    fun setPump(on: Boolean)
    fun setArea(area: IrrigationArea)
    fun setLedState(led: Led, on: Boolean)
    fun displayLine(lineNr: Int, line: String)
    fun registerSwitchListener(switchListener: ButtonListener)
}

