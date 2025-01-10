package com.vdzon.java.model

data class Schedule (
    val startSchedule: Timestamp,
    val endSchedule: Timestamp?,
    val duration: Int,
    val daysInterval: Int,
    val erea:WateringArea,
    val enabled: Boolean
)

