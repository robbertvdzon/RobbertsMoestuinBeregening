package com.vdzon.irrigation.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ScheduleTest {
    val startTimestamp = Timestamp(2000, 1, 1, 12, 0)
    val endtimestamp = Timestamp(2001, 1, 1, 12, 0)
    val schedule_with_interval_1 = Schedule("", startTimestamp, endtimestamp, 0, 1, IrrigationArea.MOESTUIN, true)
    val schedule_with_interval_2 = Schedule("", startTimestamp, endtimestamp, 0, 2, IrrigationArea.MOESTUIN, true)
    val schedule_with_interval_3 = Schedule("", startTimestamp, endtimestamp, 0, 3, IrrigationArea.MOESTUIN, true)
    val disabled_schedule = Schedule("", startTimestamp, endtimestamp, 0, 3, IrrigationArea.MOESTUIN, false)

    @Test
    fun `on start date, before 12_00, returns same day 12_00`() {
        val fromTime = Timestamp(2000, 1, 1, 11, 0).plusDays(0)
        val expectedScheduleTime = startTimestamp
        val nextSchedule = schedule_with_interval_1.findFirstSchedule(fromTime)
        assertThat(nextSchedule).isEqualTo(expectedScheduleTime)
    }

    @Test
    fun `on start date, after 12_00, returns next day 12_00`() {
        val fromTime = Timestamp(2000, 1, 1, 13, 0).plusDays(0)
        val expectedScheduleTime = startTimestamp.plusDays(1)
        val nextSchedule = schedule_with_interval_1.findFirstSchedule(fromTime)
        assertThat(nextSchedule).isEqualTo(expectedScheduleTime)
    }

    @Test
    fun `on start date +1, before 12_00, returns start day+1 12_00`() {
        val fromTime = Timestamp(2000, 1, 1, 11, 0).plusDays(1)
        val expectedScheduleTime = startTimestamp.plusDays(1)
        val nextSchedule = schedule_with_interval_1.findFirstSchedule(fromTime)
        assertThat(nextSchedule).isEqualTo(expectedScheduleTime)
    }

    @Test
    fun `on start date +1, after 12_00, returns start day+2 12_00`() {
        val fromTime = Timestamp(2000, 1, 1, 13, 0).plusDays(1)
        val expectedScheduleTime = startTimestamp.plusDays(2)
        val nextSchedule = schedule_with_interval_1.findFirstSchedule(fromTime)
        assertThat(nextSchedule).isEqualTo(expectedScheduleTime)
    }

    @Test
    fun `on year before start date, before 12_00, returns start day 12_00`() {
        val fromTime = Timestamp(1999, 1, 1, 11, 0)
        val expectedScheduleTime = startTimestamp
        val nextSchedule = schedule_with_interval_1.findFirstSchedule(fromTime)
        assertThat(nextSchedule).isEqualTo(expectedScheduleTime)
    }

    @Test
    fun `on year before start date, after 12_00, returns start day 12_00`() {
        val fromTime = Timestamp(1999, 1, 1, 13, 0)
        val expectedScheduleTime = startTimestamp
        val nextSchedule = schedule_with_interval_1.findFirstSchedule(fromTime)
        assertThat(nextSchedule).isEqualTo(expectedScheduleTime)
    }

    @Test
    fun `on day after end date, return null`() {
        val fromTime = Timestamp(2001, 1, 1, 13, 0)
        val nextSchedule = schedule_with_interval_1.findFirstSchedule(fromTime)
        assertThat(nextSchedule).isNull()
    }

    @Test
    fun `on start date, before 12_00, with interval 2, returns same day 12_00`() {
        val fromTime = Timestamp(2000, 1, 1, 11, 0).plusDays(0)
        val expectedScheduleTime = startTimestamp
        val nextSchedule = schedule_with_interval_2.findFirstSchedule(fromTime)
        assertThat(nextSchedule).isEqualTo(expectedScheduleTime)
    }

    @Test
    fun `on start date, after 12_00, with interval 2, returns day+2 12_00`() {
        val fromTime = Timestamp(2000, 1, 1, 13, 0).plusDays(0)
        val expectedScheduleTime = startTimestamp.plusDays(2)
        val nextSchedule = schedule_with_interval_2.findFirstSchedule(fromTime)
        assertThat(nextSchedule).isEqualTo(expectedScheduleTime)
    }

    @Test
    fun `on start date+1, after 12_00, with interval 2, returns day+2 12_00`() {
        val fromTime = Timestamp(2000, 1, 1, 13, 0).plusDays(1)
        val expectedScheduleTime = startTimestamp.plusDays(2)
        val nextSchedule = schedule_with_interval_2.findFirstSchedule(fromTime)
        assertThat(nextSchedule).isEqualTo(expectedScheduleTime)
    }

    @Test
    fun `on start date+1, before 12_00, with interval 2, returns day+2 12_00`() {
        val fromTime = Timestamp(2000, 1, 1, 11, 0).plusDays(1)
        val expectedScheduleTime = startTimestamp.plusDays(2)
        val nextSchedule = schedule_with_interval_2.findFirstSchedule(fromTime)
        assertThat(nextSchedule).isEqualTo(expectedScheduleTime)
    }

    @Test
    fun `on start date, after 12_00, with interval 3, returns day+3 12_00`() {
        val fromTime = Timestamp(2000, 1, 1, 13, 0).plusDays(0)
        val expectedScheduleTime = startTimestamp.plusDays(3)
        val nextSchedule = schedule_with_interval_3.findFirstSchedule(fromTime)
        assertThat(nextSchedule).isEqualTo(expectedScheduleTime)
    }

    @Test
    fun `on start date+100, before 12_00, with interval 3, returns day+102 12_00`() {
        val fromTime = Timestamp(2000, 1, 1, 11, 0).plusDays(100)
        val expectedScheduleTime = Timestamp(2000, 1, 1, 12, 0).plusDays(102)
        val nextSchedule = schedule_with_interval_3.findFirstSchedule(fromTime)
        assertThat(nextSchedule).isEqualTo(expectedScheduleTime)
    }

    @Test
    fun `on disabled schedule, return false`() {
        val fromTime = Timestamp(2000, 1, 1, 11, 0)
        val nextSchedule = disabled_schedule.findFirstSchedule(fromTime)
        assertThat(nextSchedule).isNull()
    }




}