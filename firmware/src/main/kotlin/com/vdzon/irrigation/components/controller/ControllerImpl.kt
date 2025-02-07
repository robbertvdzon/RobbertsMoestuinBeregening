package com.vdzon.irrigation.components.controller

import com.fasterxml.jackson.databind.ObjectMapper
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

const val DISABLE_PUMP_TIME_WHILE_CHANGING_AREA = 0L // when the area was changed, disable the pump for 30 seconds

class ControllerImpl(
    private val hardware: Hardware,
    private val firebaseProducer: FirebaseProducer,
    private val log: Log,
    private val objectMapper: ObjectMapper
) : Controller {
    private var requestedState: State = State()
    private var schedules: Schedules = Schedules()
    private var currentIP: String = "Unknown"
    private var disablePumpUntil = LocalDateTime.now() // when the area was changed, disable the pump for 30 seconds
    private var lastKnownIrrigationArea: IrrigationArea? = null

    override fun start() {
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
        if (requestedState.closeTime.isBefore(now)) {
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
        log.logInfo("Update schedules.json : $schedules")
        val file = File("schedules.json")
        objectMapper.writeValue(file, schedules)
    }

    fun saveState() {
        log.logInfo("Update state.json")
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
            sleep(millis = 100)
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
            try {
                checkSchedules()
            }
            catch (e: Exception){
                e.printStackTrace()
            }
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
                requestedState.irrigationArea = schedule.area
                saveState()
            }
        }
        // check if a schedule needs to be removed
        val schedulesToRemove = schedules.schedules.filter {
            it.endDate != null &&
                    it.endDate.isBefore(now)
        }
        if (schedulesToRemove.isNotEmpty()) {
            schedules.schedules.removeAll(schedulesToRemove)
            saveSchedule()
        }
    }

    private fun ensurePumpState() {
        val pumpDisabled = LocalDateTime.now().isBefore(disablePumpUntil)
        if (pumpDisabled) {
            // disable the pump, and flash the lights
            hardware.setPump(false)
            val flashToggle = LocalDateTime.now().second.mod(2) == 0
            hardware.setLedState(Led.PUMP_ON, flashToggle)
            hardware.setLedState(Led.PUMP_OFF, !flashToggle)
        } else {
            // pump is not disabled, check what state is needed
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
    }


    private fun ensureIrrigationAreState() {
        if (lastKnownIrrigationArea != requestedState.irrigationArea) {
            disablePumpUntil = LocalDateTime.now().plusSeconds(DISABLE_PUMP_TIME_WHILE_CHANGING_AREA)
        }
        hardware.setArea(requestedState.irrigationArea)
        hardware.setLedState(Led.GAZON_AREA, requestedState.irrigationArea == IrrigationArea.GAZON)
        hardware.setLedState(Led.MOESTUIN_AREA, requestedState.irrigationArea == IrrigationArea.MOESTUIN)
        lastKnownIrrigationArea = requestedState.irrigationArea
    }

    private fun updateDisplay() {
        val pumpDisabled = LocalDateTime.now().isBefore(disablePumpUntil)
        val changeAreText = if (pumpDisabled) "(WISSEL)" else ""
        val aliveChar = getAliveChar()
        hardware.displayLine(1, "$currentIP $aliveChar")
        hardware.displayLine(2, "${requestedState.irrigationArea.name} $changeAreText")
        hardware.displayLine(3, "${getNextSchedule()}")
        hardware.displayLine(4, "${getTimerTime()}")
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

        val dayOfWeek = firstTimestamp.getReadableDay()
        val hour = if (firstTimestamp.hour < 10) "0${firstTimestamp.hour}" else "${firstTimestamp.hour}"
        val minute = if (firstTimestamp.minute < 10) "0${firstTimestamp.minute}" else "${firstTimestamp.minute}"
        val area = mapArea(firstSchedule.area)
        val time = "$hour:$minute"
        return "$dayOfWeek$time, $area"
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