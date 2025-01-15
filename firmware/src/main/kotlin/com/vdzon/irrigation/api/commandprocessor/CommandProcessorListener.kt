package com.vdzon.irrigation.api.commandprocessor

import com.vdzon.irrigation.api.model.Schedule
import com.vdzon.irrigation.api.model.IrrigationArea

interface CommandProcessorListener {
    fun updateState()
    fun addIrrigationTime(minutes: Int)
    fun changeIrrigationArea(area: IrrigationArea)
    fun addSchedule(schedule: Schedule)
    fun removeSchedule(id: String)
}