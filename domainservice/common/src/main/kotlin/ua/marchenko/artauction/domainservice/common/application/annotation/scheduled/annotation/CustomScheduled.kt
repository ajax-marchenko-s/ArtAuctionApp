package ua.marchenko.artauction.domainservice.common.application.annotation.scheduled.annotation

import ua.marchenko.artauction.domainservice.common.application.annotation.scheduled.scheduledDetails.enums.Day

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class CustomScheduled(val day: Day, val hours: Int = 0, val minutes: Int = 0, val seconds: Int = 0)
