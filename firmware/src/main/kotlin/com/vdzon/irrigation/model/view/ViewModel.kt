package com.vdzon.irrigation.model.view

import com.vdzon.irrigation.model.EnrichedSchedule
import com.vdzon.irrigation.model.PumpStatus
import com.vdzon.irrigation.model.Timestamp
import com.vdzon.irrigation.model.WateringArea

data class ViewModel  (
    val pumpStatus: PumpStatus,
    val currentWateringArea: WateringArea,
    val pumpingEndTime: Timestamp,
    val schedules: List<EnrichedSchedule>,
    val nextSchedule: EnrichedSchedule,
)