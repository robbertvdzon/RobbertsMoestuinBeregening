package com.vdzon.irrigation.components.hardwaresimulation

import com.vdzon.irrigation.api.hardware.Button
import com.vdzon.irrigation.api.hardware.ButtonListener
import com.vdzon.irrigation.api.hardware.Hardware
import com.vdzon.irrigation.api.hardware.Led
import com.vdzon.irrigation.api.log.Log
import com.vdzon.irrigation.api.model.IrrigationArea
import org.jline.terminal.TerminalBuilder
import kotlin.concurrent.thread


class HardwareSimulation(
    private val log: Log
) : Hardware {
    private var buttonListener: ButtonListener? = null
    override fun start() {
        thread(start = true) {
            startHardware()
        }
    }

    fun startHardware() {
        val terminal = TerminalBuilder.terminal()
        log.logInfo("q:-5  w:+5     e:moesttuin  r:gazon")
        while (true) {
            val key = terminal.reader().read().toChar()
            when (key) {
                'q' -> {
                    buttonListener?.onButtonClick(Button.MIN_5_MINUTES)
                }

                'w' -> {
                    buttonListener?.onButtonClick(Button.PLUS_5_MINUTES)
                }

                'e' -> {
                    buttonListener?.onButtonClick(Button.MOESTUIN_AREA)
                }

                'r' -> {
                    buttonListener?.onButtonClick(Button.GAZON_AREA)
                }
            }
        }
    }

    private var lastPumpState: Boolean? = null
    override fun setPump(pumpState: Boolean) {
        when {
            pumpState -> if (lastPumpState == false) log.logInfo("PUMP ON")
            !pumpState -> if (lastPumpState == true) log.logInfo("PUMP OFF")
        }
        lastPumpState = pumpState
    }

    private var lastArea: IrrigationArea? = null
    override fun setArea(area: IrrigationArea) {
        when (area) {
            IrrigationArea.MOESTUIN -> if (lastArea != IrrigationArea.MOESTUIN) log.logInfo("SET AREA TO MOESTUIN")
            IrrigationArea.GAZON -> if (lastArea != IrrigationArea.GAZON) log.logInfo("SET AREA TO GAZON")
        }
        lastArea = area
    }

    private val lastLedState: MutableMap<Led, Boolean> = mutableMapOf()
    override fun setLedState(led: Led, on: Boolean) {
        if (lastLedState[led] != on) {
            log.logInfo("LED $led : $on")
        }
        lastLedState[led] = on
    }

    private val lastLine: MutableMap<Int, String> = mutableMapOf()
    override fun displayLine(lineNr: Int, line: String) {
        if (lastLine[lineNr] != line) {
            lastLine[lineNr] = line
            if (lineNr != 1) {// do not display the screen update because of the alive char, so skip line 1
                log.logInfo("--------------------")
                (1..4).forEach { lineNr ->
                    log.logInfo(lastLine[lineNr])
                }
                log.logInfo("--------------------")
            }
        }
    }

    override fun registerSwitchListener(buttonListener: ButtonListener) {
        this.buttonListener = buttonListener
    }


}
