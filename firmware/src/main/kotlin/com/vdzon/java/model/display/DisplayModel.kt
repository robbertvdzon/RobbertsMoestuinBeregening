package com.vdzon.java.model.display

import com.vdzon.java.model.EnrichedSchedule
import com.vdzon.java.model.PumpStatus
import com.vdzon.java.model.WateringArea

data class DisplayModel (
    val ipAddress: String,
    val pumpStatus: PumpStatus,
    val currentWateringArea: WateringArea,
    val nextSchedule: EnrichedSchedule,
)