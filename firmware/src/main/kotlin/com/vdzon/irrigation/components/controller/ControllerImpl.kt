package com.vdzon.irrigation.components.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.vdzon.irrigation.common.FirebaseProducer
import com.vdzon.irrigation.components.hardware.api.Button
import com.vdzon.irrigation.components.hardware.api.Hardware
import com.vdzon.irrigation.components.hardware.api.Led
import com.vdzon.irrigation.model.*
import com.vdzon.irrigation.model.view.ViewModel
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import kotlin.concurrent.thread

class ControllerImpl(
    val hardware: Hardware,
    val firebaseProducer: FirebaseProducer

) : Controller {
    // status
    private var requestedState = loadState()
    private var schedules = loadSchedules()
    private var currentIP: String = "Unknown"
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

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
        requestedState.closeTime = requestedState.closeTime.plusMinutes(minutes.toLong())
        saveState()
    }

    override fun changeIrrigationArea(area: IrrigationArea) {
        requestedState.irrigationArea = area
        saveState()
    }

    override fun addSchedule(schedule: Schedule) {
        saveSchedule()
    }

    override fun removeSchedule(id: String) {
        saveSchedule()
    }

    override fun setIP(ip: String) {
        this.currentIP = ip
    }

    fun saveSchedule() {
        val file = File("schedules.json")
        objectMapper.writeValue(file, schedules)
    }

    fun saveState() {
        val file = File("state.json")
        objectMapper.writeValue(file, requestedState)
    }

    fun loadState(): State {
        val file = File("state.json")
        try {
            return objectMapper.readValue(file, State::class.java)
        } catch (e: Exception) {
            return State()
        }
    }

    fun loadSchedules(): Schedules {
        val file = File("schedules.json")
        try {
            return objectMapper.readValue(file, Schedules::class.java)
        } catch (e: Exception) {
            return Schedules()
        }

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
        val closeTimeInFuture = requestedState.closeTime.isAfter(LocalDateTime.now())
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
        hardware.setArea(requestedState.irrigationArea)
        hardware.setLedState(Led.GAZON_AREA, requestedState.irrigationArea == IrrigationArea.GAZON)
        hardware.setLedState(Led.MOESTUIN_AREA, requestedState.irrigationArea == IrrigationArea.MOESTUIN)
    }

    private fun updateDisplay() {
        val aliveChar = getAliveChar()
        hardware.displayLine(1, "IP   : $currentIP $aliveChar")
        hardware.displayLine(2, "Area : ${requestedState.irrigationArea.name}")
        hardware.displayLine(3, "Next : ${requestedState.irrigationArea.name}")// TODO: next schedule zoeken
        hardware.displayLine(4, "Timer: ${getTimerTime()}")
    }

    private fun getAliveChar() = if (LocalDateTime.now().second % 2 == 0) "-" else "|"


    private fun getTimerTime(): String {
        val currentTime = LocalDateTime.now()
        val secondsRemainingUntilClose =
            currentTime.until(requestedState.closeTime, java.time.temporal.ChronoUnit.SECONDS)
        val closeTimeInFuture = secondsRemainingUntilClose > 0
        if (closeTimeInFuture) {
            val duration: Duration = Duration.between(currentTime, requestedState.closeTime)
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
        val closeTimeInFuture = requestedState.closeTime.isAfter(LocalDateTime.now())
        val viewModel = ViewModel(
            ipAddress = currentIP,
            pumpStatus = if (closeTimeInFuture) PumpStatus.OPEN else PumpStatus.CLOSE,
            currentIrrigationArea = requestedState.irrigationArea,
            pumpingEndTime = Timestamp.fromTime(requestedState.closeTime),
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