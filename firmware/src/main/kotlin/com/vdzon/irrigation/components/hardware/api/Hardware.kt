package com.vdzon.irrigation.components.hardware.api

import com.vdzon.irrigation.model.WateringArea

interface Hardware {
    fun start()
    fun setPump(on: Boolean)
    fun setArea(area: WateringArea)
    fun setLedState(led: Led, on: Boolean)
    fun displayLine(lineNr: Int, line: String)
    fun registerSwitchListener(switchListener: ButtonListener)
}

interface ButtonListener {
    fun onButtonClick(button: Button)
}

enum class Button {
    PLUS_5_MINUTES,
    MIN_5_MINUTES,
    MOESTUIN_AREA,
    GAZON_AREA
}

enum class Led {
    PUMP_ON,
    PUMP_OFF,
    MOESTUIN_AREA,
    GAZON_AREA
}