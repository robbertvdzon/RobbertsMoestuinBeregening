package com.vdzon.irrigation.components.hardware.mockimpl

import com.vdzon.irrigation.components.controller.KlepState
import com.vdzon.irrigation.components.hardware.api.EncoderListener
import com.vdzon.irrigation.components.hardware.api.Hardware
import com.vdzon.irrigation.components.hardware.api.KlepListener
import com.vdzon.irrigation.components.hardware.api.SwitchListener
import com.vdzon.irrigation.components.hardware.impl.DisplayData
import org.jline.terminal.TerminalBuilder
import java.util.*


class HardwareMock : Hardware {
    private var encoderListener: EncoderListener? = null
    private var switchListener: SwitchListener? = null
    private var klepListener: KlepListener? = null
    private val displayData =  DisplayData()

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


    override fun klepOpen() {
        displayData.klepState = KlepState.OPEN
        klepListener?.klepOpen()
        println("klep open")
    }

    override fun klepClose() {
        displayData.klepState = KlepState.CLOSED
        klepListener?.klepClosed()
//        println("klep close")
    }

    override fun updateTime(time: String) {
        displayData.time = time
    }

    override fun updateIP(ip: String) {
        displayData.ip = ip
        println("ip: $ip")
    }
    override fun updateKlepState(klepState: KlepState){
        displayData.klepState = klepState
//        println("klepState: $klepState")
    }


    override fun encoderUp(){
        encoderListener?.encoderUp()
    }
    override fun encoderDown(){
        encoderListener?.encoderDown()
    }
//    override fun switchOn(){
//        switchListener?.switchOn()
//    }
//    override fun switchOff(){
//        switchListener?.switchOff()
//    }
    override fun registerEncoderListener(encoderListener: EncoderListener){
        this.encoderListener = encoderListener
    }
    override fun registerSwitchListener(switchListener: SwitchListener){
        this.switchListener = switchListener
    }
    override fun registerKlepListener(klepListener: KlepListener){
        this.klepListener = klepListener
    }
    override fun getDisplayData(): DisplayData = displayData




}
