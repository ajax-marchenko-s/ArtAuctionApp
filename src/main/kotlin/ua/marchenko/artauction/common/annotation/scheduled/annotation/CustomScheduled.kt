package ua.marchenko.artauction.common.annotation.scheduled.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class CustomScheduled(val schedule: String)
