package com.vdzon.java.model.required

import com.vdzon.java.model.PumpStatus
import com.vdzon.java.model.Timestamp
import com.vdzon.java.model.WateringArea

data class RequiredModel  (
    val pumpStatus: PumpStatus,
    val currentWateringArea: WateringArea,
    val pumpingEndTime: Timestamp,
)