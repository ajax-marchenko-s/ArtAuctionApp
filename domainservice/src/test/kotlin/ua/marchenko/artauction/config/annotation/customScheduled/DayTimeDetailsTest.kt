package ua.marchenko.artauction.config.annotation.customScheduled

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Duration
import java.time.LocalDateTime
import org.junit.jupiter.api.Assertions.assertEquals
import ua.marchenko.artauction.common.annotation.scheduled.scheduledDetails.DayTimeDetails
import ua.marchenko.artauction.common.annotation.scheduled.scheduledDetails.enums.Day

class DayTimeDetailsTest {

    @Test
    fun `should throw exception when provided invalid hour`() {
        // WHEN //THEN
        val exception = assertThrows<IllegalArgumentException> {
            DayTimeDetails(Day.MONDAY, 24, 0, 0)
        }
        assertEquals("Hours must be between 0 and 23. Provided: 24", exception.message)
    }

    @Test
    fun `should throw exception when provided invalid minute`() {
        // WHEN //THEN
        val exception = assertThrows<IllegalArgumentException> {
            DayTimeDetails(Day.MONDAY, 0, 60, 0)
        }
        assertEquals("Minutes must be between 0 and 59. Provided: 60", exception.message)
    }

    @Test
    fun `should throw exception when provided invalid second`() {
        // WHEN //THEN
        val exception = assertThrows<IllegalArgumentException> {
            DayTimeDetails(Day.MONDAY, 0, 0, 60)
        }
        assertEquals("Seconds must be between 0 and 59. Provided: 60", exception.message)
    }

    @Test
    fun `should calculate time difference for same day when dayTimeDetails is in future`() {
        // GIVEN
        val dayTimeDetails = DayTimeDetails(Day.MONDAY, 10, 30, 0) // monday 10:30
        val localDateTime = LocalDateTime.of(2024, 9, 2, 9, 30, 0) // monday 9:30

        // WHEN
        val duration = dayTimeDetails.calculateTimeDifference(localDateTime)

        // THEN
        assertEquals(Duration.ofHours(1), duration)
    }

    @Test
    fun `should calculate time difference for same day when dayTimeDetails is in past`() {
        //GIVEN
        val dayTimeDetails = DayTimeDetails(Day.MONDAY, 8, 30, 0) // monday 8:30
        val localDateTime = LocalDateTime.of(2024, 9, 2, 9, 30, 0) // monday 9:30

        //WHEN
        val duration = dayTimeDetails.calculateTimeDifference(localDateTime)

        //THEN
        assertEquals(Duration.ofDays(6).plusHours(23), duration)
    }

    @Test
    fun `should calculate time difference when dayTimeDetails is different day than localDateTime`() {
        // GIVEN
        val dayTimeDetails = DayTimeDetails(Day.MONDAY, 8, 30, 0) // monday 8:30
        val localDateTime = LocalDateTime.of(2024, 9, 4, 9, 30, 0) // wednesday 9:30

        // WHEN
        val duration = dayTimeDetails.calculateTimeDifference(localDateTime)

        // THEN
        assertEquals(Duration.ofDays(4).plusHours(23), duration)
    }

    @Test
    fun `should calculate time difference when dayTimeDetails is in past and dayTimeDetails is all`() {
        //GIVEN
        val dayTimeDetails = DayTimeDetails(Day.ALL, 8, 30, 0) // all 8:30
        val localDateTime = LocalDateTime.of(2024, 9, 2, 9, 30, 0) // monday 9:30

        // WHEN
        val duration = dayTimeDetails.calculateTimeDifference(localDateTime)

        // THEN
        assertEquals(Duration.ofHours(23), duration)
    }

    @Test
    fun `should calculate time difference when dayTimeDetails is in future and dayTimeDetails is all`() {
        //GIVEN
        val dayTimeDetails = DayTimeDetails(Day.ALL, 10, 30, 0) // all 10:30
        val localDateTime = LocalDateTime.of(2024, 9, 2, 9, 30, 0) // monday 9:30

        // WHEN
        val duration = dayTimeDetails.calculateTimeDifference(localDateTime)

        // THEN
        assertEquals(Duration.ofHours(1), duration)
    }

    @Test
    fun `should calculate duration between when day is ALL`() {
        //GIVEN
        val dayTimeDetails = DayTimeDetails(Day.ALL, 10, 0, 0) // all 10:00

        //WHEN
        val duration = dayTimeDetails.calculateDurationBetween()

        //THEN
        assertEquals(Duration.ofDays(1), duration)
    }

    @Test
    fun `should calculate duration between when day is specific day`() {
        // GIVEN
        val dayTimeDetails = DayTimeDetails(Day.WEDNESDAY, 10, 0, 0) // wednesday 10:00

        //WHEN
        val duration = dayTimeDetails.calculateDurationBetween()

        // THEN
        assertEquals(Duration.ofDays(7), duration)
    }
}
