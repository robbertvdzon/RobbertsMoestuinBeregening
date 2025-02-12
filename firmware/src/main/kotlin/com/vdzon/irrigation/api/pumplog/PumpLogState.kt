package com.vdzon.irrigation.api.pumplog

data class PumpLogState(
    val minutes: Int,
    val log: List<PumpLogItem>
)

data class PumpLogItem(
    val hour: Int,
    val minute: Int
)

