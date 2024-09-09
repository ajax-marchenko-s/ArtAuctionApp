package ua.marchenko.artauction.config.annotation.customScheduled

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import config.annotation.customProfiling.CustomScheduledTestServiceWithAnnotationImpl
import config.annotation.customProfiling.CustomScheduledTestServiceWithoutAnnotationImpl
import getRandomString
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.event.ContextRefreshedEvent
import ua.marchenko.artauction.common.annotation.scheduled.contextListener.CustomScheduledInvokerContextListener
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.slf4j.LoggerFactory

class CustomScheduledAnnotationTest {

    @MockK
    private lateinit var beanFactory: ConfigurableListableBeanFactory

    @MockK
    private lateinit var context: ApplicationContext

    @MockK
    private lateinit var scheduledExecutorService: ScheduledExecutorService

    @MockK
    private lateinit var beanDefinition: BeanDefinition

    @InjectMockKs
    private lateinit var customScheduledInvokerContextListener: CustomScheduledInvokerContextListener

    private lateinit var testLogAppender: ListAppender<ILoggingEvent>

    @BeforeEach
    fun setUp() {
        val field = customScheduledInvokerContextListener::class.declaredMemberProperties
            .first { it.name == SCHEDULED_EXECUTOR_SERVICE_NAME }
        field.isAccessible = true
        field.javaField?.set(customScheduledInvokerContextListener, scheduledExecutorService)

        val logger = LoggerFactory.getLogger(CustomScheduledInvokerContextListener::class.java) as Logger
        testLogAppender = ListAppender()
        testLogAppender.start()
        logger.addAppender(testLogAppender)
    }

    @Test
    fun `should schedule method when CustomScheduled annotation is present`() {
        // GIVEN
        val beanName = getRandomString()
        val beanNames = arrayOf(beanName)
        val testBean = CustomScheduledTestServiceWithAnnotationImpl()

        every { context.beanDefinitionNames } returns beanNames
        every { beanFactory.getBeanDefinition(beanName) } returns beanDefinition
        every { beanDefinition.beanClassName } returns CustomScheduledTestServiceWithAnnotationImpl::class.java.name
        every { context.getBean(beanName) } returns testBean
        every { scheduledExecutorService.scheduleWithFixedDelay(any(), any(), any(), TimeUnit.SECONDS) } returns null

        // WHEN
        customScheduledInvokerContextListener.onApplicationEvent(ContextRefreshedEvent(context))

        // THEN
        verify(exactly = 1) {
            scheduledExecutorService.scheduleWithFixedDelay(any(), any(), any(), TimeUnit.SECONDS)
        }
        assertTrue(testLogAppender.list.isNotEmpty(), "Log event must contain logs")
        assertNotNull(testLogAppender.list.find { it.message.contains(SCHEDULED_PART_LOG_MESSAGE_TEMPLATE) })
        assertEquals(Level.INFO, testLogAppender.list.find {
            it.message.contains(SCHEDULED_PART_LOG_MESSAGE_TEMPLATE)
        }?.level)
    }


    @Test
    fun `should not schedule method when CustomScheduled annotation is not present`() {
        // GIVEN
        val beanName = getRandomString()
        val beanNames = arrayOf(beanName)
        val testBean = CustomScheduledTestServiceWithoutAnnotationImpl()

        every { context.beanDefinitionNames } returns beanNames
        every { beanFactory.getBeanDefinition(beanName) } returns beanDefinition
        every { beanDefinition.beanClassName } returns CustomScheduledTestServiceWithoutAnnotationImpl::class.java.name
        every { context.getBean(beanName) } returns testBean

        // WHEN
        customScheduledInvokerContextListener.onApplicationEvent(ContextRefreshedEvent(context))

        // THEN
        verify(exactly = 0) { scheduledExecutorService.scheduleWithFixedDelay(any(), any(), any(), TimeUnit.SECONDS) }
        assertTrue(testLogAppender.list.isEmpty(), "Log event found in NOT annotated method")
    }

    companion object {
        private const val SCHEDULED_EXECUTOR_SERVICE_NAME = "scheduledService"
        private const val SCHEDULED_PART_LOG_MESSAGE_TEMPLATE = "has added to ScheduledExecutorService with delay"
    }
}
