package com.vdzon.irrigation.api.pumplog

import java.time.LocalDateTime

data class PumpLogState(
    val minutes: Int,
    val log: List<PumpLogItem>
)

data class PumpLogItem(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int
) {
    companion object {
        fun getNewLogItem(): PumpLogItem {
            val now = LocalDateTime.now()
            return PumpLogItem(
                now.year,
                now.monthValue,
                now.dayOfMonth,
                now.hour,
                now.minute
            )

        }
    }
}

