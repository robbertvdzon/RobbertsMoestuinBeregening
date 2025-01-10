package com.vdzon.irrigation.model


data class EnrichedSchedule (
    val schedule: Schedule,
    val nextRun: Timestamp?,// calculate this one
)

