package com.vdzon.irrigation.api.model

import java.time.temporal.ChronoUnit

data class Schedule(
    val id: String,
    val startDate: ScheduleDate,
    val endDate: ScheduleDate?,
    val scheduledTime: ScheduleTime,
    val duration: Int,
    val daysInterval: Int,
    val erea: IrrigationArea,
    val enabled: Boolean
) {

    fun toEnrichedSchedule(): EnrichedSchedule {
        return EnrichedSchedule(this, findFirstSchedule(Timestamp.now()))
    }

    fun findFirstSchedule(from: Timestamp): Timestamp? {
        if (!enabled) return null
        val endDateSchedule = endDate?.getTimestampAt(23, 59)// on the end of the last day
        if (endDateSchedule != null && from.isAfterOrEqual(endDateSchedule)) return null
        // first try, the date of the from Timestamp, with the time of the startSchedule
        var timestampToTry = Timestamp(
            year = from.year,
            month = from.month,
            day = from.day,
            hour = scheduledTime.hour,
            minute = scheduledTime.minute,
            second = 0
        )
        while (true) {
            val startTimeSchedule = startDate.getTimestampAt(0, 0)
            val startDate = startTimeSchedule.toLocalDateTime().toLocalDate()
            val date = timestampToTry.toLocalDateTime().toLocalDate()
            val daysSinceStartDate = ChronoUnit.DAYS.between(startDate, date)
            if (
                from.isBeforeOrEqual(timestampToTry) &&
                startTimeSchedule.isBeforeOrEqual(timestampToTry) &&
                daysSinceStartDate.mod(daysInterval) == 0
            ) return timestampToTry
            timestampToTry = timestampToTry.plusDays(1)
        }
    }
}

