package ua.marchenko.artauction.common.annotation.scheduled.annotation

import ua.marchenko.artauction.common.annotation.scheduled.scheduledDetails.enums.Day

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class CustomScheduled(val day: Day, val hours: Int = 0, val minutes: Int = 0, val seconds: Int = 0)
