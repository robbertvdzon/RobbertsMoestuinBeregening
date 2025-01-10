package com.vdzon.java.model.view

import com.vdzon.java.model.EnrichedSchedule
import com.vdzon.java.model.PumpStatus
import com.vdzon.java.model.Timestamp
import com.vdzon.java.model.WateringArea

data class ViewModel  (
    val pumpStatus: PumpStatus,
    val currentWateringArea: WateringArea,
    val pumpingEndTime: Timestamp,
    val schedules: List<EnrichedSchedule>,
    val nextSchedule: EnrichedSchedule,
)