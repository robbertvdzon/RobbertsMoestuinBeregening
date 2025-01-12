package com.vdzon.irrigation.components.controller

import com.vdzon.irrigation.common.FirebaseProducer
import com.vdzon.irrigation.components.commandprocessor.api.CommandProcessorListener
import com.vdzon.irrigation.components.hardware.api.Button
import com.vdzon.irrigation.components.hardware.api.Hardware
import com.vdzon.irrigation.components.hardware.api.ButtonListener
import com.vdzon.irrigation.components.network.api.NetworkListener
import com.vdzon.irrigation.model.*
import com.vdzon.irrigation.model.view.ViewModel
import java.time.LocalDateTime
import kotlin.concurrent.thread
import java.time.Duration

class Controller(
    val hardware: Hardware,
    val firebaseProducer: FirebaseProducer

) : ButtonListener, CommandProcessorListener, NetworkListener {
    // status
    private var requestedCloseTime: LocalDateTime = LocalDateTime.now()
    private var requestedIrrigationArea: IrrigationArea = IrrigationArea.MOESTUIN
    private var currentIP: String = "Unknown"

    init {
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
    fun updateHardwareThread() {
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
    fun checkSchedulesThread() {
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
        } else {
            hardware.setPump(false)
        }
    }

    private fun ensureIrrigationAreState() {
        hardware.setArea(requestedIrrigationArea)
    }

    private fun updateDisplay() {
        val currentTime = LocalDateTime.now()
        val secondsRemainingUntilClose = currentTime.until(requestedCloseTime, java.time.temporal.ChronoUnit.SECONDS)
        val closeTimeInFuture = secondsRemainingUntilClose > 0
        if (closeTimeInFuture) {
            val duration: Duration = Duration.between(currentTime, requestedCloseTime)
            val hours = duration.toHours()
            val minutes = duration.toMinutesPart()
            val seconds = duration.toSecondsPart()
            val formattedDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            hardware.displayLine(3, "$formattedDuration")
        } else {
            hardware.displayLine(3, "")
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


enum class KlepState(val text: String) {
    OPEN("Klep open"), CLOSED("Klep dicht")
}