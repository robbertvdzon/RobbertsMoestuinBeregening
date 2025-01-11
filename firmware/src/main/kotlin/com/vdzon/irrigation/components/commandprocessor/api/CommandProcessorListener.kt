package com.vdzon.irrigation.components.commandprocessor.api

import com.vdzon.irrigation.model.Schedule
import com.vdzon.irrigation.model.IrrigationArea

interface CommandProcessorListener {
    fun addStopTime(minutes: Int)
    fun changeIrrigationArea(area: IrrigationArea)
    fun addSchedule(schedule: Schedule)
    fun removeSchedule(id: String)
}