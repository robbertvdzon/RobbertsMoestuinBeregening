package com.vdzon.irrigation.model.display

import com.vdzon.irrigation.model.EnrichedSchedule
import com.vdzon.irrigation.model.PumpStatus
import com.vdzon.irrigation.model.IrrigationArea

data class DisplayModel (
    val ipAddress: String,
    val pumpStatus: PumpStatus,
    val currentIrrigationArea: IrrigationArea,
    val nextSchedule: EnrichedSchedule,
)