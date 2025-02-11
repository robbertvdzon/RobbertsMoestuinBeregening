package com.vdzon.irrigation.api.model.view

import com.vdzon.irrigation.api.model.EnrichedSchedule
import com.vdzon.irrigation.api.model.PumpStatus
import com.vdzon.irrigation.api.model.Timestamp
import com.vdzon.irrigation.api.model.IrrigationArea
import com.vdzon.irrigation.api.model.ValveStatus

data class ViewModel  (
    val ipAddress: String,
    val pumpStatus: PumpStatus,
    val currentIrrigationArea: IrrigationArea,
    val valveStatus: ValveStatus,
    val pumpingEndTime: Timestamp,
    val schedules: List<EnrichedSchedule>,
    val nextSchedule: String,
)