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

    override fun setLedState(led: Led, on: Boolean) {
        when {
            led == Led.PUMP_OFF && on -> println("LED 'PUMP_OFF' : on")
            led == Led.PUMP_OFF && !on -> println("LED 'PUMP_OFF' : off")

            led == Led.PUMP_ON && on -> println("LED 'PUMP_ON' : on")
            led == Led.PUMP_ON && !on -> println("LED 'PUMP_ON' : off")

            led == Led.MOESTUIN_AREA && on -> println("LED 'MOESTUIN' : on")
            led == Led.MOESTUIN_AREA && !on -> println("LED 'MOESTUIN' : off")

            led == Led.GAZON_AREA && on -> println("LED 'GAZON' : on")
            led == Led.GAZON_AREA && !on -> println("LED 'GAZON' : off")
        }
    }

    override fun displayLine(lineNr: Int, line: String) {
//        println("DISPLAY, LINE $lineNr : $line")
    }

    override fun registerSwitchListener(buttonListener: ButtonListener) {
        this.buttonListener = buttonListener
    }


}
