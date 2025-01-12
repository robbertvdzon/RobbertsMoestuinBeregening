package com.vdzon.irrigation.model

import java.time.LocalDateTime

data class Timestamp (
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
){
    companion object{
        fun fromTime(dateTime: LocalDateTime): Timestamp{
            val year = dateTime.year
            val month = dateTime.monthValue
            val day = dateTime.dayOfMonth
            val hour = dateTime.hour
            val minute = dateTime.minute
            return Timestamp(year, month, day, hour, minute)
        }

        fun now(): Timestamp = fromTime(LocalDateTime.now())
    }
}