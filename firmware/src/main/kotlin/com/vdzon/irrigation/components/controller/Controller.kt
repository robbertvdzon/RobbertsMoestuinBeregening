package com.vdzon.irrigation.components.controller

import com.vdzon.irrigation.common.FirebaseProducer
import com.vdzon.irrigation.components.hardware.api.Button
import com.vdzon.irrigation.components.hardware.api.Hardware
import com.vdzon.irrigation.components.hardware.api.ButtonListener
import java.time.LocalDateTime
import kotlin.concurrent.thread
import java.time.Duration

class Controller(
    val hardware: Hardware,
    val firebaseProducer: FirebaseProducer

) : ButtonListener {
    private var closeTime: LocalDateTime = LocalDateTime.now().minusDays(1)// in the past
    private var updateStatusUntilTime: LocalDateTime = LocalDateTime.now()


    init {
        hardware.registerSwitchListener(this)
        thread(start = true) {
            updateStatusThread()
        }
    }

    override fun onButtonClick(button: Button){
        when(button){
            Button.MIN_5_MINUTES -> println("min 5")
            Button.PLUS_5_MINUTES -> println("plus 5")
            Button.MOESTUIN_AREA -> println("moestuin area")
            Button.GAZON_AREA -> println("gazon area")
        }

    }

//    fun getDisplayData(): DisplayData {
//        return hardware.getDisplayData()
//    }

    fun setIp(ip: String) {
//        hardware.updateIP(ip)
    }

    fun openKlep() {
//        hardware.klepOpen()
    }

    fun closeKlep() {
//        hardware.klepClose()
    }

//    override fun klepOpen() {
//        klepState= KlepState.OPEN
//        hardware.updateKlepState(KlepState.OPEN)
//        firebaseProducer.setStatus("Open")
//    }
//
//    override fun klepClosed() {
//        klepState= KlepState.CLOSED
//        hardware.updateKlepState(KlepState.CLOSED)
//        firebaseProducer.setStatus("Closed")
//
//    }

    fun startUpdating(){
        val currentTime = LocalDateTime.now()
        val nextHalfHour: LocalDateTime = currentTime.plusMinutes(2)// laten staan op 2 minuten
        updateStatusUntilTime = nextHalfHour
        println("Update the time status until ${updateStatusUntilTime}")
    }
//
//    override fun encoderUp(amount: Int) {
//        if (closeTime.isBefore(LocalDateTime.now())) {
//            closeTime = LocalDateTime.now().plusMinutes(amount.toLong())
//        } else {
//            closeTime = closeTime.plusMinutes(amount.toLong())
//        }
//        val time = closeTime.toString().substring(11, 16)
//        updateStatus()
//    }
//
//    override fun dicht() {
//        closeTime = LocalDateTime.now()
//        val time = closeTime.toString().substring(11, 16)
//        updateStatus()
//    }
//
//    override fun encoderDown(amount: Int) {
//        closeTime = closeTime.minusMinutes(amount.toLong())
//        val time = closeTime.toString().substring(11, 16)
//        updateStatus()
//    }


    fun updateStatusThread() {
        checkKlep()
        updateStatus()
        while (true) {
            sleep()
            checkKlep()
            updateStatus()
        }
    }

    private fun checkKlep() {
        val currentTime = LocalDateTime.now()
        val secondsRemainingUntilClose = currentTime.until(closeTime, java.time.temporal.ChronoUnit.SECONDS)
        val closeTimeInFuture = secondsRemainingUntilClose>0
        val closeTimeInPast = !closeTimeInFuture
        if (closeTimeInPast) {
            hardware.setPump(false)
        }
        else{
            hardware.setPump(true)
        }
    }

    private fun updateStatus() {
        updateDisplay()
        updateFirebase()
    }

    private fun updateDisplay() {
        val currentTime = LocalDateTime.now()
        if (currentTime.isAfter(updateStatusUntilTime)) return// do not update unless requested
        val secondsRemainingUntilClose = currentTime.until(closeTime, java.time.temporal.ChronoUnit.SECONDS)
        val closeTimeInFuture = secondsRemainingUntilClose>0
        if (closeTimeInFuture){
            val duration: Duration = Duration.between(currentTime, closeTime)
            val hours = duration.toHours()
            val minutes = duration.toMinutesPart()
            val seconds = duration.toSecondsPart()
            val formattedDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            hardware.displayLine(3,"$formattedDuration")
            firebaseProducer.setTime(formattedDuration)
        }else{
            hardware.displayLine(3,"")
            firebaseProducer.setTime("00:00")
        }
    }

    private fun updateFirebase() {
        val currentTime = LocalDateTime.now()
        if (currentTime.isAfter(updateStatusUntilTime)) return// do not update unless requested
        val secondsRemainingUntilClose = currentTime.until(closeTime, java.time.temporal.ChronoUnit.SECONDS)
        val closeTimeInFuture = secondsRemainingUntilClose>0
        if (closeTimeInFuture){
            val duration: Duration = Duration.between(currentTime, closeTime)
            val hours = duration.toHours()
            val minutes = duration.toMinutesPart()
            val seconds = duration.toSecondsPart()
            val formattedDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            firebaseProducer.setTime(formattedDuration)
        }else{
            firebaseProducer.setTime("00:00")
        }
    }


        private fun sleep() {
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
        }
    }


}



enum class KlepState(val text: String) {
    OPEN ("Klep open"), CLOSED ("Klep dicht")
}