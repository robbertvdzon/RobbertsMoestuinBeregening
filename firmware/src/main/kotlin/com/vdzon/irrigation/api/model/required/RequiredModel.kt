package com.vdzon.irrigation.api.model.required

import com.vdzon.irrigation.api.model.PumpStatus
import com.vdzon.irrigation.api.model.Timestamp
import com.vdzon.irrigation.api.model.IrrigationArea

data class RequiredModel  (
    val pumpStatus: PumpStatus,
    val currentIrrigationArea: IrrigationArea,
    val pumpingEndTime: Timestamp,
)