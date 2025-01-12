package com.vdzon.irrigation.components.controller

import com.vdzon.irrigation.common.FirebaseProducer
import com.vdzon.irrigation.components.commandprocessor.api.CommandProcessorListener
import com.vdzon.irrigation.components.hardware.api.Button
import com.vdzon.irrigation.components.hardware.api.Hardware
import com.vdzon.irrigation.components.hardware.api.ButtonListener
import com.vdzon.irrigation.components.hardware.api.Led
import com.vdzon.irrigation.components.network.api.NetworkListener
import com.vdzon.irrigation.model.*
import com.vdzon.irrigation.model.view.ViewModel
import java.time.LocalDateTime
import kotlin.concurrent.thread
import java.time.Duration

class ControllerImpl(
    val hardware: Hardware,
    val firebaseProducer: FirebaseProducer

) : Controller {
    // status
    private var requestedCloseTime: LocalDateTime = LocalDateTime.now()
    private var requestedIrrigationArea: IrrigationArea = IrrigationArea.MOESTUIN
    private var currentIP: String = "Unknown"

    override fun start() {
        thread(start = true) {
            updateHardwareThread()
        }
        thread(start = true) {
            checkSchedulesThread()
        }
    }

    override fun onButtonClick(button: Button) {
        when (button) {
            Button.MIN_5_MINUTES -> {
                println("min 5")
                addStopTime(-5)
            }

            Button.PLUS_5_MINUTES -> {
                println("plus 5")
                addStopTime(5)
            }

            Button.MOESTUIN_AREA -> {
                println("moestuin area")
                changeIrrigationArea(IrrigationArea.MOESTUIN)
            }

            Button.GAZON_AREA -> {
                println("gazon area")
                changeIrrigationArea(IrrigationArea.GAZON)
            }
        }
    }

    override fun addStopTime(minutes: Int) {
        requestedCloseTime = requestedCloseTime.plusMinutes(minutes.toLong())
    }

    override fun changeIrrigationArea(area: IrrigationArea) {
        this.requestedIrrigationArea = area
    }

    override fun addSchedule(schedule: Schedule) {

    }

    override fun removeSchedule(id: String) {

    }

    override fun setIP(ip: String) {
        this.currentIP = ip
    }


    /*
    Make sure that the output of the pi is the same as the requested state
     */
    private fun updateHardwareThread() {
        while (true) {
            ensurePumpState()
            ensureIrrigationAreState()
            updateDisplay()
            updateFirebase()
            sleep()
        }
    }

    /*
    Check once a minute to see if a schedule needs to be started
     */
    private fun checkSchedulesThread() {
        while (true) {
            checkSchedules()
            sleep(1000 * 60)// sleep for a minute
        }
    }

    private fun checkSchedules() {
        /*
        Check schedules, if the time (in minutes) is the start time of a schedule, then set
        the requiredStop time and requiredArea
         */
    }

    private fun ensurePumpState() {
        val closeTimeInFuture = requestedCloseTime.isAfter(LocalDateTime.now())
        if (closeTimeInFuture) {
            hardware.setPump(true)
            hardware.setLedState(Led.PUMP_ON, true)
            hardware.setLedState(Led.PUMP_OFF, false)
        } else {
            hardware.setPump(false)
            hardware.setLedState(Led.PUMP_OFF, true)
            hardware.setLedState(Led.PUMP_ON, false)
        }
    }

    private fun ensureIrrigationAreState() {
        hardware.setArea(requestedIrrigationArea)
        hardware.setLedState(Led.GAZON_AREA, requestedIrrigationArea == IrrigationArea.GAZON)
        hardware.setLedState(Led.MOESTUIN_AREA, requestedIrrigationArea == IrrigationArea.MOESTUIN)
    }

    private fun updateDisplay() {
        val aliveChar = getAliveChar()
        hardware.displayLine(1, "IP   : $currentIP $aliveChar")
        hardware.displayLine(2, "Area : ${requestedIrrigationArea.name}")
        hardware.displayLine(3, "Next : ${requestedIrrigationArea.name}")// TODO: next schedule zoeken
        hardware.displayLine(4, "Timer: ${getTimerTime()}")
    }

    private fun getAliveChar() = if (LocalDateTime.now().second % 2 == 0) "-" else "|"


    private fun getTimerTime(): String {
        val currentTime = LocalDateTime.now()
        val secondsRemainingUntilClose = currentTime.until(requestedCloseTime, java.time.temporal.ChronoUnit.SECONDS)
        val closeTimeInFuture = secondsRemainingUntilClose > 0
        if (closeTimeInFuture) {
            val duration: Duration = Duration.between(currentTime, requestedCloseTime)
            val hours = duration.toHours()
            val minutes = duration.toMinutesPart()
            val seconds = duration.toSecondsPart()
            val formattedDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            return formattedDuration
        } else {
            return "00:00"
        }

    }

    private fun updateFirebase() {
        val closeTimeInFuture = requestedCloseTime.isAfter(LocalDateTime.now())
        val viewModel = ViewModel(
            ipAddress = currentIP,
            pumpStatus = if (closeTimeInFuture) PumpStatus.OPEN else PumpStatus.CLOSE,
            currentIrrigationArea = requestedIrrigationArea,
            pumpingEndTime = Timestamp.fromTime(requestedCloseTime),
            schedules = emptyList(),
            nextSchedule = null,
        )
        firebaseProducer.setState(viewModel)
    }

    private fun sleep(millis: Long = 1000) {
        try {
            Thread.sleep(millis)
        } catch (e: InterruptedException) {
        }
    }
}