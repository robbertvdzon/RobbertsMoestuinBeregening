package com.vdzon.irrigation.model

import java.time.LocalDateTime
import java.time.ZoneOffset

data class Timestamp (
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
){

    fun isAfterOrEqual(other: Timestamp): Boolean{
        val thisLocalDateTime = this.toLocalDateTime()
        val otherLocalDateTime = other.toLocalDateTime()
        return thisLocalDateTime.isAfter(otherLocalDateTime) || thisLocalDateTime.isEqual(otherLocalDateTime)
    }

    fun isBeforeOrEqual(other: Timestamp): Boolean{
        val thisLocalDateTime = this.toLocalDateTime()
        val otherLocalDateTime = other.toLocalDateTime()
        return thisLocalDateTime.isBefore(otherLocalDateTime) || thisLocalDateTime.isEqual(otherLocalDateTime)
    }
    fun toLocalDateTime(): LocalDateTime = LocalDateTime.of(year, month, day, hour, minute)

    fun plusDays(days: Long): Timestamp = fromTime(this.toLocalDateTime().plusDays(days))
    fun toEpochSecond() = toLocalDateTime().toEpochSecond(ZoneOffset.UTC)

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


        fun buildTimestamp(
            year: Int?,
            month: Int?,
            day: Int?,
            hour: Int?,
            minute: Int?
        ):Timestamp? {
            if (year==null) return null
            if (month==null) return null
            if (day==null) return null
            if (hour==null) return null
            if (minute==null) return null
            return Timestamp(year, month, day, hour, minute)
        }
    }
}