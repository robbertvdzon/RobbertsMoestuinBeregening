package com.vdzon.irrigation.api.model.display

import com.vdzon.irrigation.api.model.EnrichedSchedule
import com.vdzon.irrigation.api.model.PumpStatus
import com.vdzon.irrigation.api.model.IrrigationArea

data class DisplayModel (
    val ipAddress: String,
    val pumpStatus: PumpStatus,
    val currentIrrigationArea: IrrigationArea,
    val nextSchedule: EnrichedSchedule,
)