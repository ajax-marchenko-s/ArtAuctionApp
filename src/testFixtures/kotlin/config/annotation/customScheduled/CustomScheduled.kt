package config.annotation.customScheduled

import ua.marchenko.artauction.common.annotation.scheduled.annotation.CustomScheduled
import ua.marchenko.artauction.common.annotation.scheduled.scheduledDetails.enums.Day

interface CustomScheduledTestService {
    fun test()
    fun test(s: Int)
}

class CustomScheduledTestServiceWithoutAnnotationImpl : CustomScheduledTestService {
    override fun test() {
        println("I don't have annotation")
    }

    override fun test(s: Int) {
        println("I don't have annotation: $s")
    }
}

class CustomScheduledTestServiceWithAnnotationImpl : CustomScheduledTestService {
    @CustomScheduled(day = Day.MONDAY, hours = 12, minutes = 0, seconds = 0)
    override fun test() {
        println("I have annotation")
    }

    override fun test(s: Int) {
        println("I don't have annotation: $s")
    }
}
