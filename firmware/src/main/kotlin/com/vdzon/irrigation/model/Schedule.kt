package com.vdzon.irrigation.model

import java.time.temporal.ChronoUnit

data class Schedule (
    val id: String,
    val startSchedule: Timestamp,
    val endSchedule: Timestamp?,
    val duration: Int,
    val daysInterval: Int,
    val erea:IrrigationArea,
    val enabled: Boolean
){
    fun findFirstSchedule(from: Timestamp): Timestamp?{
        if (!enabled) return null
        if (endSchedule!=null && from.isAfterOrEqual(endSchedule)) return null
        // first try, the date of the from Timestamp, with the time of the startSchedule
        var timestampToTry = Timestamp(
            year = from.year,
            month = from.month,
            day = from.day,
            hour = startSchedule.hour,
            minute = startSchedule.minute
        )
        while (true){
            val startDate = startSchedule.toLocalDateTime().toLocalDate()
            val date = timestampToTry.toLocalDateTime().toLocalDate()
            val daysSinceStartDate =  ChronoUnit.DAYS.between(startDate, date)
            if (
                from.isBeforeOrEqual(timestampToTry) &&
                startSchedule.isBeforeOrEqual(timestampToTry) &&
                daysSinceStartDate.mod(daysInterval)==0
                ) return timestampToTry
            timestampToTry = timestampToTry.plusDays(1)
        }
    }
}

