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
            listener?.addStopTime(count)
        }

        if (command.startsWith("ADD_SCHEDULE")) {
            val parts = command.split(",")
            println("ADD_SCHEDULE, with ${parts.size} parts : command=$command")
            if (parts.size == 16) {
                val id = parts[1]
                val duration = parts[2]
                val daysInterval = parts[3]
                val erea = parts[4]
                val enabled = parts[5]
                val startYear = parts[6]
                val startMonth = parts[7]
                val startDay = parts[8]
                val startHour = parts[9]
                val startMinute = parts[10]
                val endYear = parts[11]
                val endMonth = parts[12]
                val endDay = parts[13]
                val endHour = parts[14]
                val endMinute = parts[15]
            }
            val startSchedule: Timestamp = Timestamp(2025, 1, 1, 13, 30)
            val endSchedule: Timestamp? = null
            val schedule = Schedule(
                "001",
                startSchedule,
                endSchedule,
                45,
                1,
                IrrigationArea.GAZON,
                true
            )
            listener?.addSchedule(schedule)
        }

        if (command.startsWith("TEST1")) {
            println("ADD_SCHEDULES, TEST")
            val startSchedule: Timestamp = Timestamp(2025, 1, 1, 13, 30)
            val endSchedule: Timestamp? = null
            val schedule = Schedule(
                "001",
                startSchedule,
                endSchedule,
                45,
                1,
                IrrigationArea.GAZON,
                true
            )
            listener?.addSchedule(schedule)
        }


        if (command.startsWith("TEST2")) {
            println("ADD_SCHEDULES, TEST2")
            val startSchedule: Timestamp = Timestamp(2025, 1, 1, 21, 30)
            val endSchedule: Timestamp? = null
            val schedule = Schedule(
                "002",
                startSchedule,
                endSchedule,
                50,
                1,
                IrrigationArea.MOESTUIN,
                true
            )
            listener?.addSchedule(schedule)
        }

    }

    fun registerListener(listener: CommandProcessorListener) {
        this.listener = listener

    }
}