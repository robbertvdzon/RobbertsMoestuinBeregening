package com.vdzon.irrigation.api.model

import java.time.LocalDateTime
import java.time.ZoneOffset

data class ScheduleDate(
    val year: Int,
    val month: Int,
    val day: Int,
) {
    fun getTimestampAt(hour: Int, minute: Int) = Timestamp(year, month, day, hour, minute)

    fun isAfterOrEqual(timestamp: Timestamp): Boolean {
        val todayAtMidnight = getTimestampAt(23, 59)
        return todayAtMidnight.isAfterOrEqual(timestamp)

    }

    fun isBefore(timestamp: Timestamp): Boolean {
        val todayAtMidnight = getTimestampAt(0, 0)
        return todayAtMidnight.isBefore(timestamp)

    }
}

data class ScheduleTime(
    val hour: Int,
    val minute: Int,
)

data class Timestamp(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int
) {
    fun isAfterOrEqual(other: Timestamp): Boolean {
        val thisLocalDateTime = this.toLocalDateTime()
        val otherLocalDateTime = other.toLocalDateTime()
        return thisLocalDateTime.isAfter(otherLocalDateTime) || thisLocalDateTime.isEqual(otherLocalDateTime)
    }

    fun isBeforeOrEqual(other: Timestamp): Boolean {
        val thisLocalDateTime = this.toLocalDateTime()
        val otherLocalDateTime = other.toLocalDateTime()
        return thisLocalDateTime.isBefore(otherLocalDateTime) || thisLocalDateTime.isEqual(otherLocalDateTime)
    }

    fun isBefore(other: Timestamp): Boolean {
        val thisLocalDateTime = this.toLocalDateTime()
        val otherLocalDateTime = other.toLocalDateTime()
        return thisLocalDateTime.isBefore(otherLocalDateTime)
    }

    fun toLocalDateTime(): LocalDateTime = LocalDateTime.of(year, month, day, hour, minute)

    fun plusDays(days: Long): Timestamp = fromTime(this.toLocalDateTime().plusDays(days))

    fun toEpochSecond() = toLocalDateTime().toEpochSecond(ZoneOffset.UTC)

    companion object {
        fun fromTime(dateTime: LocalDateTime): Timestamp {
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