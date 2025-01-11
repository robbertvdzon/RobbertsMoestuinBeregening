package com.vdzon.irrigation.model

data class Schedule (
    val id: String,
    val startSchedule: Timestamp,
    val endSchedule: Timestamp?,
    val duration: Int,
    val daysInterval: Int,
    val erea:IrrigationArea,
    val enabled: Boolean
)

