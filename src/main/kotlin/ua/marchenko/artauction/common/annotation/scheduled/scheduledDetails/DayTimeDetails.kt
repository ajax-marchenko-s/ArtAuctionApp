package ua.marchenko.artauction.common.annotation.scheduled.scheduledDetails

import java.time.LocalDateTime
import java.time.Duration
import ua.marchenko.artauction.common.annotation.scheduled.scheduledDetails.enums.Day

data class DayTimeDetails(
    val day: Day,
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
) {

    init {
        @Suppress("MagicNumber")
        require(hours in 0..23) { "Hours must be between 0 and 23. Provided: $hours" }
        @Suppress("MagicNumber")
        require(minutes in 0..59) { "Minutes must be between 0 and 59. Provided: $minutes" }
        @Suppress("MagicNumber")
        require(seconds in 0..59) { "Seconds must be between 0 and 59. Provided: $seconds" }
    }

    fun calculateTimeDifference(localDateTime: LocalDateTime): Duration {
        val dayDifference =
            if (day == Day.ALL) 0 else ((day.ordinal - localDateTime.dayOfWeek.ordinal) + WEEK_LENGTH) % WEEK_LENGTH

        var targetTime =
            LocalDateTime.of(localDateTime.year, localDateTime.month, localDateTime.dayOfMonth, hours, minutes, seconds)
                .plusDays(dayDifference.toLong())

        if (targetTime.isBefore(localDateTime)) {
            targetTime = targetTime.plusDays(if (day == Day.ALL) 1 else WEEK_LENGTH.toLong())
        }
        return Duration.between(localDateTime, targetTime)
    }

    fun calculateDurationBetween(): Duration =
        if (day == Day.ALL) Duration.ofDays(1) else Duration.ofDays(WEEK_LENGTH.toLong())

    companion object {
        private const val WEEK_LENGTH = 7
    }
}

