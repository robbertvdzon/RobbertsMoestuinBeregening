package com.vdzon.irrigation.model.required

import com.vdzon.irrigation.model.PumpStatus
import com.vdzon.irrigation.model.Timestamp
import com.vdzon.irrigation.model.WateringArea

data class RequiredModel  (
    val pumpStatus: PumpStatus,
    val currentWateringArea: WateringArea,
    val pumpingEndTime: Timestamp,
)