package com.vdzon.irrigation.model.required

import com.vdzon.irrigation.model.PumpStatus
import com.vdzon.irrigation.model.Timestamp
import com.vdzon.irrigation.model.IrrigationArea

data class RequiredModel  (
    val pumpStatus: PumpStatus,
    val currentIrrigationArea: IrrigationArea,
    val pumpingEndTime: Timestamp,
)