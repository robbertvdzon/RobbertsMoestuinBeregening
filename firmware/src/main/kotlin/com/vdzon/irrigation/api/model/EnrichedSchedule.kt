package com.vdzon.irrigation.api.model


data class EnrichedSchedule (
    val schedule: Schedule,
    val nextRun: Timestamp?,// calculate this one
)

