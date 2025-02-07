package com.vdzon.irrigation.components.commandprocessor

import com.vdzon.irrigation.api.commandprocessor.CommandProcessor
import com.vdzon.irrigation.api.commandprocessor.CommandProcessorListener
import com.vdzon.irrigation.api.log.Log
import com.vdzon.irrigation.api.model.IrrigationArea
import com.vdzon.irrigation.api.model.Schedule
import com.vdzon.irrigation.api.model.ScheduleDate
import com.vdzon.irrigation.api.model.ScheduleTime
import com.vdzon.irrigation.api.model.Timestamp

class BewateringCommandProcessor(
    private val log: Log
) : CommandProcessor {
    private var listener: CommandProcessorListener? = null

    override fun process(command: String) {
        log.logInfo("Processing command: $command")
        val count = command.toIntOrNull()
        if (count != null) {
            listener?.addIrrigationTime(count)
        }

        if (command.startsWith("UPDATE_IRRIGATION_TIME")) {
            val parts = command.split(",")
            log.logInfo("UPDATE_IRRIGATION_TIME, with ${parts.size} parts : command=$command")
            if (parts.size == 2) {
                val extraMinutes = parts[1].toIntOrNull()
                if (extraMinutes != null) listener?.addIrrigationTime(extraMinutes)
            }
        }

        if (command.startsWith("CHANGE_AREA")) {
            val parts = command.split(",")
            log.logInfo("CHANGE_AREA, with ${parts.size} parts : command=$command")
            if (parts.size == 2) {
                val area = IrrigationArea.valueOf(parts[1])
                listener?.changeIrrigationArea(area)
            }
        }

        if (command.startsWith("UPDATE_STATE")) {
            log.logInfo("UPDATE_STATE")
            listener?.updateState()
        }

        if (command.startsWith("REMOVE_SCHEDULE")) {
            val parts = command.split(",")
            log.logInfo("REMOVE_SCHEDULE, with ${parts.size} parts : command=$command")
            if (parts.size == 2) {
                val id = parts[1]
                listener?.removeSchedule(id)
            }
        }

        if (command.startsWith("ADD_SCHEDULE")) {
            val parts = command.split(",")
            log.logInfo("ADD_SCHEDULE, with ${parts.size} parts : command=$command")
            var index: Int = 1
            if (parts.size == 14) {
                val id = parts[index++]
                val duration = parts[index++].toInt()
                val daysInterval = parts[index++].toInt()
                val area = IrrigationArea.valueOf(parts[index++])
                val enabled = parts[index++].toBoolean()
                val startYear = parts[index++].toInt()
                val startMonth = parts[index++].toInt()
                val startDay = parts[index++].toInt()
                val endYear = parts[index++].toIntOrNull()
                val endMonth = parts[index++].toIntOrNull()
                val endDay = parts[index++].toIntOrNull()
                val scheduleHour = parts[index++].toInt()
                val scheduleMinute = parts[index++].toInt()

                val startDate: ScheduleDate = ScheduleDate(startYear, startMonth, startDay)
                val endDate: ScheduleDate? = if (endYear != null && endMonth != null && endDay != null) ScheduleDate(endYear, endMonth, endDay) else null
                val scheduleTime = ScheduleTime(scheduleHour, scheduleMinute)
                val schedule = Schedule(
                    id,
                    startDate,
                    endDate,
                    scheduleTime,
                    duration,
                    daysInterval,
                    area,
                    enabled
                )
                listener?.addSchedule(schedule)
            }
        }
    }

    fun registerListener(listener: CommandProcessorListener) {
        this.listener = listener
    }
}