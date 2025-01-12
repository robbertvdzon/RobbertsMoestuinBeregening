package com.vdzon.irrigation.components.hardware.mockimpl

import com.vdzon.irrigation.components.hardware.api.*
import com.vdzon.irrigation.model.IrrigationArea
import org.jline.terminal.TerminalBuilder
import kotlin.concurrent.thread


class HardwareMock : Hardware {
    private var buttonListener: ButtonListener? = null
    override fun start() {
        thread(start = true) {
            startHardware()
        }
    }

    fun startHardware() {
        val terminal = TerminalBuilder.terminal()
        println("q:-5  w:+5     e:moesttuin  r:gazon")
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
            pumpState -> if (lastPumpState == false) println("PUMP ON")
            !pumpState -> if (lastPumpState == true) println("PUMP OFF")
        }
        lastPumpState = pumpState
    }

    private var lastArea: IrrigationArea? = null
    override fun setArea(area: IrrigationArea) {
        when (area) {
            IrrigationArea.MOESTUIN -> if (lastArea != IrrigationArea.MOESTUIN) println("SET AREA TO MOESTUIN")
            IrrigationArea.GAZON -> if (lastArea != IrrigationArea.GAZON) println("SET AREA TO GAZON")
        }
        lastArea = area
    }

    private val lastLedState: MutableMap<Led, Boolean> = mutableMapOf()
    override fun setLedState(led: Led, on: Boolean) {
        if (lastLedState[led] != on) {
            println("LED $led : $on")
        }
        lastLedState[led] = on
    }

    private val lastLine: MutableMap<Int, String> = mutableMapOf()
    override fun displayLine(lineNr: Int, line: String) {
        if (lastLine[lineNr] != line) {
            lastLine[lineNr] = line
            if (lineNr != 1) {// do not display the screen update because of the alive char, so skip line 1
                (1..4).forEach { lineNr ->
                    println(lastLine[lineNr])
                }
            }
        }
    }

    override fun registerSwitchListener(buttonListener: ButtonListener) {
        this.buttonListener = buttonListener
    }


}
