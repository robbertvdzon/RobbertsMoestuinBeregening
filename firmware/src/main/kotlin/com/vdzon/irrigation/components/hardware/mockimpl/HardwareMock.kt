package com.vdzon.irrigation.components.hardware.mockimpl

import com.vdzon.irrigation.components.hardware.api.*
import com.vdzon.irrigation.model.WateringArea
import org.jline.terminal.TerminalBuilder


class HardwareMock : Hardware {
    private var buttonListener: ButtonListener? = null

    override fun start(){
        val terminal = TerminalBuilder.terminal()
        println("q:-5  w:+5     e:moesttuin  r:gazon")
        while (true) {
            val key = terminal.reader().read().toChar()
            when(key){
                'q' -> {
                    println("-5")
                }
                'w' -> {
                    println("+5")
                }
                'e' -> {
                    println("moestuin")
                }
                'r' -> {
                    println("gazon")
                }
            }
        }
    }

    override fun setPump(on: Boolean) {
        when {
            on -> println("PUMP ON")
            !on -> println("PUMP OFF")
        }
    }

    override fun setArea(area: WateringArea) {
        when (area) {
            WateringArea.MOESTUIN -> println("SET AREA TO MOESTUIN")
            WateringArea.GAZON -> println("SET AREA TO GAZON")
        }
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

    override fun displayLine(lineNr: Int, line: String){
        println("DISPLAY, LINE $lineNr : $line")
    }

    override fun registerSwitchListener(buttonListener: ButtonListener){
        this.buttonListener = buttonListener
    }



}
