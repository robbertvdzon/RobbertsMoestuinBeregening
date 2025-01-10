package com.vdzon.irrigation.model.display

import com.vdzon.irrigation.model.EnrichedSchedule
import com.vdzon.irrigation.model.PumpStatus
import com.vdzon.irrigation.model.WateringArea

data class DisplayModel (
    val ipAddress: String,
    val pumpStatus: PumpStatus,
    val currentWateringArea: WateringArea,
    val nextSchedule: EnrichedSchedule,
)