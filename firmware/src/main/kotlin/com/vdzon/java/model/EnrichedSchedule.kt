package com.vdzon.java.model


data class EnrichedSchedule (
    val schedule: Schedule,
    val nextRun: Timestamp?,// calculate this one
)

