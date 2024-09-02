package config.annotation.customProfiling

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import java.util.concurrent.TimeUnit
import ua.marchenko.artauction.common.annotation.profiling.annotation.CustomProfiling

interface CustomProfilingTestService {
    fun test(s: String)
    fun test(s: Int)
    fun errorTest(s: String)
}

class CustomProfilingTestServiceWithoutAnnotationImpl : CustomProfilingTestService {
    override fun test(s: String) {
        println("I don't have annotation: $s")
    }

    override fun test(s: Int) {
        println("I don't have annotation in class without annotation method: $s")
    }

    override fun errorTest(s: String) {
        throw RuntimeException("I dont have annotation and throw an exception")
    }
}

class CustomProfilingTestServiceWithAnnotationImpl : CustomProfilingTestService {

    @CustomProfiling(timeUnit = TimeUnit.MICROSECONDS)
    override fun test(s: String) {
        println("I have annotation: $s")
    }

    override fun test(s: Int) {
        println("I don't have annotation in class with annotation method: $s")
    }

    @CustomProfiling
    override fun errorTest(s: String) {
        throw RuntimeException("I have annotation and throw an exception")
    }
}

class MockLogger : AppenderBase<ILoggingEvent>() {
    val logs = mutableListOf<ILoggingEvent>()

    override fun append(event: ILoggingEvent) {
        logs.add(event)
    }
}
