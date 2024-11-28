package ua.marchenko.artauction.domainservice.common.application.annotation.customScheduled

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Profile
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import ua.marchenko.artauction.domainservice.common.application.annotation.scheduled.annotation.CustomScheduled
import ua.marchenko.artauction.domainservice.common.application.annotation.scheduled.contextListener.CustomScheduledInvokerContextListener
import ua.marchenko.artauction.domainservice.common.application.annotation.scheduled.scheduledDetails.enums.Day
import ua.marchenko.artauction.domainservice.utils.AbstractBaseIntegrationTest

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
        assertTrue(
            testLogAppender.list.any {
                it.formattedMessage.contains("annotatedMethod $SCHEDULED_PART_LOG_MESSAGE_TEMPLATE")
            }, "Expected log with scheduling message not found"
        )
    }

    @Test
    fun `should not schedule method when CustomScheduled annotation is not present`() {
        // WHEN
        customScheduledInvokerContextListener.onApplicationEvent(ContextRefreshedEvent(context))

        // THEN
        assertFalse(
            testLogAppender.list.any {
                it.formattedMessage.contains("notAnnotatedMethod $SCHEDULED_PART_LOG_MESSAGE_TEMPLATE")
            }, "No methods without CustomScheduled annotation should be scheduled"
        )
    }

    companion object {
        private const val SCHEDULED_PART_LOG_MESSAGE_TEMPLATE = "has added to ScheduledExecutorService with delay"
    }
}

interface CustomScheduledTestService {
    fun annotatedMethod()
    fun notAnnotatedMethod()
}

@Component
@Profile("test")
class CustomScheduledTestServiceWithAnnotationImpl : CustomScheduledTestService {

    @CustomScheduled(day = Day.MONDAY, hours = 12, minutes = 0, seconds = 0)
    override fun annotatedMethod() {
        println("I have annotation")
    }

    override fun notAnnotatedMethod() {
        println("I don't have annotation")
    }
}
