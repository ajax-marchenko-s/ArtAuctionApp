package ua.marchenko.artauction.common.annotation.scheduled.cron

import java.time.LocalDateTime
import java.time.Duration
import ua.marchenko.artauction.common.extension.valueOfOrNull
import ua.marchenko.artauction.common.annotation.scheduled.cron.enums.Day

data class DayTimeDetails(val day: Day, val hour: Int, val minute: Int, val second: Int) {

    fun calculateTimeDifference(localDateTime: LocalDateTime): Duration {
        val dayDifference =
            if (day == Day.ALL) 0 else ((day.ordinal - localDateTime.dayOfWeek.ordinal) + WEEK_LENGTH) % WEEK_LENGTH

        var targetTime =
            LocalDateTime.of(localDateTime.year, localDateTime.month, localDateTime.dayOfMonth, hour, minute, second)
                .plusDays(dayDifference.toLong())

        if (targetTime.isBefore(localDateTime)) {
            targetTime = targetTime.plusDays(if (day == Day.ALL) 1 else WEEK_LENGTH.toLong())
        }
        return Duration.between(localDateTime, targetTime)
    }

    fun calculateDurationBetween(): Duration =
        if (day == Day.ALL) Duration.ofDays(1) else Duration.ofDays(WEEK_LENGTH.toLong())

    companion object {
        const val WEEK_LENGTH = 7

        @Suppress("MagicNumber")
        fun parseTimeString(timeString: String): DayTimeDetails? {
            val parts = timeString.split(Regex("[\\s,;:|]+"))
            if (parts.size < 3) return null
            val day = if (parts[0] == "*") Day.ALL else valueOfOrNull<Day>(parts[0].uppercase())
            val hour = parts[1].toIntOrNull()
            val minute = parts[2].toIntOrNull()
            val second = parts.getOrNull(3)?.toIntOrNull() ?: 0
            return if (isValidTime(day, hour, minute, second)) {
                DayTimeDetails(day!!, hour!!, minute!!, second)
            } else {
                null
            }
        }

        @Suppress("MagicNumber")
        private fun isValidTime(day: Day?, hour: Int?, minute: Int?, second: Int): Boolean {
            val isDayInvalid = day == null
            val isHourInvalid = hour == null || hour > 24 || hour < 0
            val isMinuteInvalid = minute == null || minute > 59 || minute < 0
            val isSecondInvalid = second < 0 || second > 59
            return !(isDayInvalid || isHourInvalid || isMinuteInvalid || isSecondInvalid)
        }
    }
}
