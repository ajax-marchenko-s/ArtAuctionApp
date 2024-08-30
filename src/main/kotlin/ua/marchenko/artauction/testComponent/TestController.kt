package ua.marchenko.artauction.testComponent

import java.util.concurrent.TimeUnit
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ua.marchenko.artauction.common.annotation.profiling.annotation.CustomProfiling
import ua.marchenko.artauction.common.annotation.scheduled.annotation.CustomScheduled
import ua.marchenko.artauction.common.annotation.scheduled.scheduledDetails.enums.Day

//NOTE: this is controller to test annotation due to methods from bl are not implemented yet
@RestController
@RequestMapping("/api/v1/test")
@Profile("test")
class TestController(val testService: TestService) {

    @GetMapping
    fun test(): String {
        return testService.test()
    }
}

interface TestService {
    fun test(): String
}

@Service
class TestServiceImpl : TestService {
    @CustomScheduled(day = Day.ALL, hours = 16, minutes = 43)
    @CustomProfiling(timeUnit = TimeUnit.MICROSECONDS)
    override fun test(): String {
        println("TestService")
        return "TestService"
    }
}
