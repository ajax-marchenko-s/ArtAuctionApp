package ua.marchenko.artauction.domainservice.common.application.annotation.profiling.annotation

import java.util.concurrent.TimeUnit

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class CustomProfiling(val timeUnit: TimeUnit = TimeUnit.NANOSECONDS)
