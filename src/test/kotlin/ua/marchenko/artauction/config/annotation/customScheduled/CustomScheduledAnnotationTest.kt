package ua.marchenko.artauction.config.annotation.customScheduled

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Profile
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import ua.marchenko.artauction.common.AbstractBaseIntegrationTest
import ua.marchenko.artauction.common.annotation.scheduled.annotation.CustomScheduled
import ua.marchenko.artauction.common.annotation.scheduled.contextListener.CustomScheduledInvokerContextListener
import ua.marchenko.artauction.common.annotation.scheduled.scheduledDetails.enums.Day

class CustomScheduledAnnotationTest : AbstractBaseIntegrationTest {

    @Autowired
    private lateinit var context: ApplicationContext

    @Autowired
    private lateinit var customScheduledInvokerContextListener: CustomScheduledInvokerContextListener

    private lateinit var testLogAppender: ListAppender<ILoggingEvent>

    @BeforeEach
    fun setUp() {
        val logger = LoggerFactory.getLogger(CustomScheduledInvokerContextListener::class.java) as Logger
        testLogAppender = ListAppender()
        testLogAppender.start()
        logger.addAppender(testLogAppender)
    }

    @Test
    fun `should schedule method when CustomScheduled annotation is present`() {
        // WHEN
        customScheduledInvokerContextListener.onApplicationEvent(ContextRefreshedEvent(context))

        // THEN
        assertTrue(testLogAppender.list.count { it.message.contains(SCHEDULED_PART_LOG_MESSAGE_TEMPLATE) } > 0,
            "testLogAppender should contains at least one message")
    }

    companion object {
        private const val SCHEDULED_PART_LOG_MESSAGE_TEMPLATE = "has added to ScheduledExecutorService with delay"
    }
}

interface CustomScheduledTestService {
    fun testTest()
    fun test(s: Int)
}

@Component
@Profile("test")
class CustomScheduledTestServiceWithAnnotationImpl : CustomScheduledTestService {

    @CustomScheduled(day = Day.MONDAY, hours = 12, minutes = 0, seconds = 0)
    override fun testTest() {
        println("I have annotation")
    }

    override fun test(s: Int) {
        println("I don't have annotation: $s")
    }
}
