package com.vdzon.irrigation.components.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.vdzon.irrigation.api.controller.Controller
import com.vdzon.irrigation.api.firebase.FirebaseProducer
import com.vdzon.irrigation.api.hardware.Button
import com.vdzon.irrigation.api.hardware.Hardware
import com.vdzon.irrigation.api.hardware.Led
import com.vdzon.irrigation.api.log.Log
import com.vdzon.irrigation.api.model.*
import com.vdzon.irrigation.api.model.view.ViewModel
import java.io.File
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class ControllerImpl(
    private val hardware: Hardware,
    private val firebaseProducer: FirebaseProducer,
    private val log: Log
) : Controller {
    private var requestedState: State = State()
    private var schedules: Schedules = Schedules()
    private var currentIP: String = "Unknown"
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    override fun start() {
        objectMapper.registerModule(JavaTimeModule())
        requestedState = loadState()
        schedules = loadSchedules()
        thread(start = true) {
            updateHardwareThread()
        }
        startSchedulesThread()
    }

    override fun onButtonClick(button: Button) {
        when (button) {
            Button.MIN_5_MINUTES -> {
                log.logInfo("min 5")
                addIrrigationTime(-5)
            }

            Button.PLUS_5_MINUTES -> {
                log.logInfo("plus 5")
                addIrrigationTime(5)
            }

            Button.MOESTUIN_AREA -> {
                log.logInfo("moestuin area")
                changeIrrigationArea(IrrigationArea.MOESTUIN)
            }

            Button.GAZON_AREA -> {
                log.logInfo("gazon area")
                changeIrrigationArea(IrrigationArea.GAZON)
            }
        }
    }

    override fun addIrrigationTime(minutes: Int) {
        val now = LocalDateTime.now()
        // When current closetime is in the past, then first change it to now
        if (requestedState.closeTime.isBefore(now)){
            requestedState.closeTime = now
        }
        requestedState.closeTime = requestedState.closeTime.plusMinutes(minutes.toLong())
        saveState()
    }

    override fun changeIrrigationArea(area: IrrigationArea) {
        requestedState.irrigationArea = area
        saveState()
    }

    override fun addSchedule(schedule: Schedule) {
        // if a schedule with this id exists, remove it forst
        schedules.schedules.removeIf { it.id == schedule.id }
        schedules.schedules.add(schedule)
        saveSchedule()
    }

    override fun removeSchedule(id: String) {
        val schedule = schedules.schedules.firstOrNull { it.id == id }
        if (schedule == null) return
        schedules.schedules.remove(schedule)
        saveSchedule()
    }

    override fun updateState() {
        firebaseProducer.cleanLastState()
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
            e.printStackTrace()
            return State()
        }
    }

    fun loadSchedules(): Schedules {
        val file = File("schedules.json")
        try {
            return objectMapper.readValue(file, Schedules::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
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
    private fun startSchedulesThread() {
        val executor = Executors.newSingleThreadScheduledExecutor()
        // Calcule the time before the next minute
        val now = LocalDateTime.now()
        val nextMinute = now.withSecond(0).withNano(0).plusMinutes(1)
        val delayInMillis = Duration.between(now, nextMinute).toMillis()

        // Start the check in the first second of every minute
        executor.scheduleAtFixedRate({
            checkSchedules()
        }, delayInMillis, 60_000, TimeUnit.MILLISECONDS)
    }

    private fun checkSchedules() {
        val now = Timestamp.now()
        // check if a schedule needs to be started
        schedules.schedules.forEach { schedule ->
            val nextSchedule = schedule.findFirstSchedule(now)
            if (now == nextSchedule) {
                val currentTime = LocalDateTime.now()
                val closeTime = currentTime
                    .plusMinutes(schedule.duration.toLong())
                requestedState.closeTime = closeTime
                requestedState.irrigationArea = schedule.erea
                saveState()
            }
        }
        // check if a schedule needs to be removed
        val schedulesToRemove = schedules.schedules.filter {
            it.endSchedule != null &&
                    it.endSchedule.isAfterOrEqual(now)
        }
        schedules.schedules.removeAll(schedulesToRemove)
        saveSchedule()
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
        hardware.displayLine(3, "Next : ${getNextSchedule()}")
        hardware.displayLine(4, "Timer: ${getTimerTime()}")
    }

    private fun getNextSchedule(): String {
        val now = Timestamp.now()
        val nextSchudules = schedules
            .schedules
            .filter { it.findFirstSchedule(now) != null }
            .filter { it.findFirstSchedule(now) != now }
            .sortedBy { it.findFirstSchedule(now)?.toEpochSecond() ?: Long.MAX_VALUE }
        val firstSchedule = nextSchudules.firstOrNull()
        val firstTimestamp = firstSchedule?.findFirstSchedule(now)
        if (firstTimestamp == null) return "Geen planning"
        val dayOfWeek = mapDayOfWeek(firstTimestamp.toLocalDateTime().dayOfWeek)
        val hour = if (firstTimestamp.hour < 10) "0${firstTimestamp.hour}" else "${firstTimestamp.hour}"
        val minute = if (firstTimestamp.minute < 10) "0${firstTimestamp.minute}" else "${firstTimestamp.minute}"
        val area = mapArea(firstSchedule.erea)
        val time = "$hour:$minute"
        return "$dayOfWeek $time, $area"
    }

    private fun mapDayOfWeek(dow: DayOfWeek) = when (dow) {
        DayOfWeek.MONDAY -> "Ma"
        DayOfWeek.TUESDAY -> "Di"
        DayOfWeek.WEDNESDAY -> "Wo"
        DayOfWeek.THURSDAY -> "Do"
        DayOfWeek.FRIDAY -> "Vr"
        DayOfWeek.SATURDAY -> "Za"
        DayOfWeek.SUNDAY -> "Zo"
    }

    private fun mapArea(area: IrrigationArea) = when (area) {
        IrrigationArea.MOESTUIN -> "Moestuin"
        IrrigationArea.GAZON -> "Gazon"
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
            schedules = schedules.schedules.map { it.toEnrichedSchedule() },
            nextSchedule = getNextSchedule(),
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