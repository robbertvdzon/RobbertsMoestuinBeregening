package com.vdzon.irrigation.model

import java.time.LocalDateTime

data class State (
    var closeTime: LocalDateTime = LocalDateTime.now(),
    var irrigationArea: IrrigationArea = IrrigationArea.MOESTUIN
)