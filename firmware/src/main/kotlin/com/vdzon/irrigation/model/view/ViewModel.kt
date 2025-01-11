package com.vdzon.irrigation.model.view

import com.vdzon.irrigation.model.EnrichedSchedule
import com.vdzon.irrigation.model.PumpStatus
import com.vdzon.irrigation.model.Timestamp
import com.vdzon.irrigation.model.IrrigationArea

data class ViewModel  (
    val ipAddress: String,
    val pumpStatus: PumpStatus,
    val currentIrrigationArea: IrrigationArea,
    val pumpingEndTime: Timestamp,
    val schedules: List<EnrichedSchedule>,
    val nextSchedule: EnrichedSchedule?,
)