package com.vdzon.irrigation.components.commandprocessor.impl

import com.vdzon.irrigation.components.commandprocessor.api.CommandProcessor
import com.vdzon.irrigation.components.commandprocessor.api.CommandProcessorListener
import com.vdzon.irrigation.model.IrrigationArea
import com.vdzon.irrigation.model.Schedule
import com.vdzon.irrigation.model.Timestamp


class BewateringCommandProcessor : CommandProcessor {
    private var listener: CommandProcessorListener? = null

    override fun process(command: String) {
        println("Processing command: $command")
        val count = command.toIntOrNull()
        if (count != null) {
            listener?.addIrrigationTime(count)
        }

        if (command.startsWith("UPDATE_IRRIGATION_TIME")) {
            val parts = command.split(",")
            println("UPDATE_IRRIGATION_TIME, with ${parts.size} parts : command=$command")
            if (parts.size == 2) {
                val extraMinutes = parts[1].toIntOrNull()
                if (extraMinutes != null) listener?.addIrrigationTime(extraMinutes)
            }
        }

        if (command.startsWith("CHANGE_AREA")) {
            val parts = command.split(",")
            println("CHANGE_AREA, with ${parts.size} parts : command=$command")
            if (parts.size == 2) {
                val area = IrrigationArea.valueOf(parts[1])
                listener?.changeIrrigationArea(area)
            }
        }

        if (command.startsWith("UPDATE_STATE")) {
            println("UPDATE_STATE")
            listener?.updateState()
        }

        if (command.startsWith("REMOVE_SCHEDULE")) {
            val parts = command.split(",")
            println("REMOVE_SCHEDULE, with ${parts.size} parts : command=$command")
            if (parts.size == 2) {
                val id = parts[1]
                listener?.removeSchedule(id)
            }
        }

        if (command.startsWith("ADD_SCHEDULE")) {
            val parts = command.split(",")
            println("ADD_SCHEDULE, with ${parts.size} parts : command=$command")
            if (parts.size == 16) {
                val id = parts[1]
                val duration = parts[2].toInt()
                val daysInterval = parts[3].toInt()
                val area = IrrigationArea.valueOf(parts[4])
                val enabled = parts[5].toBoolean()
                val startYear = parts[6].toInt()
                val startMonth = parts[7].toInt()
                val startDay = parts[8].toInt()
                val startHour = parts[9].toInt()
                val startMinute = parts[10].toInt()
                val endYear = parts[11].toIntOrNull()
                val endMonth = parts[12].toIntOrNull()
                val endDay = parts[13].toIntOrNull()
                val endHour = parts[14].toIntOrNull()
                val endMinute = parts[15].toIntOrNull()
                val startSchedule: Timestamp =
                    Timestamp.buildTimestamp(startYear, startMonth, startDay, startHour, startMinute)!!
                val endSchedule: Timestamp? = Timestamp.buildTimestamp(endYear, endMonth, endDay, endHour, endMinute)
                val schedule = Schedule(
                    id,
                    startSchedule,
                    endSchedule,
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