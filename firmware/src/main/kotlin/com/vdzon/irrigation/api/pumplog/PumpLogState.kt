package com.vdzon.irrigation.api.pumplog

import java.time.LocalDateTime

data class SummaryPumpUsage(
    var minutesGazon: Int = 0,
    var minutesMoestuin: Int = 0,
    val years: MutableList<SummaryYearPumpUsage> = mutableListOf()
)

data class SummaryYearPumpUsage(
    var year: Int,
    var minutesGazon: Int = 0,
    var minutesMoestuin: Int = 0,
    val months: MutableList<SummaryMonthsPumpUsage> = mutableListOf()
)

data class SummaryMonthsPumpUsage(
    var year: Int,
    var month: Int,
    var minutesGazon: Int = 0,
    var minutesMoestuin: Int = 0,
    val days: MutableList<SummaryDaysPumpUsage> = getEmptyMonth(year, month)
){
    companion object{
        fun getEmptyMonth(year: Int, month: Int):MutableList<SummaryDaysPumpUsage>{
            val daysInMonth = java.time.YearMonth.of(year, month).lengthOfMonth()
            return (1..daysInMonth).map { day ->
                SummaryDaysPumpUsage(
                    year = year,
                    month = month,
                    day = day,
                    minutesGazon = 0,
                    minutesMoestuin = 0
                )
            }.toMutableList()
        }
    }
}

data class SummaryDaysPumpUsage(
    var year: Int,
    var month: Int,
    var day: Int,
    var minutesGazon: Int = 0,
    var minutesMoestuin: Int = 0,
)


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

