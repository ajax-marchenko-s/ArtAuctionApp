package ua.marchenko.artauction.config.annotation

import ch.qos.logback.classic.Level
import config.annotation.customProfiling.CustomProfilingTestServiceWithAnnotationImpl
import config.annotation.customProfiling.CustomProfilingTestServiceWithoutAnnotationImpl
import getRandomString
import java.lang.reflect.Proxy
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import config.annotation.customProfiling.CustomProfilingTestService
import io.mockk.impl.annotations.InjectMockKs
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import ua.marchenko.artauction.common.annotation.profiling.beanPostProcessor.CustomProfilingAnnotationBeanPostProcessor

class CustomProfilingAnnotationBeanPostProcessorTest {

    @InjectMockKs
    private lateinit var beanPostProcessor: CustomProfilingAnnotationBeanPostProcessor

    private lateinit var testLogAppender: ListAppender<ILoggingEvent>

    @BeforeEach
    fun setup() {
        val logger = LoggerFactory.getLogger(CustomProfilingAnnotationBeanPostProcessor::class.java) as Logger
        testLogAppender = ListAppender()
        testLogAppender.start()
        logger.addAppender(testLogAppender)
    }

    @Test
    fun `should return proxy of bean when bean has methods with custom annotation`() {
        // GIVEN
        val annotatedBean = CustomProfilingTestServiceWithAnnotationImpl()
        val annotatedBeanName = getRandomString(10)

        // WHEN
        val proxy = createProxy(annotatedBean, annotatedBeanName)

        // THEN
        assertTrue(
            Proxy.isProxyClass(proxy?.javaClass), "Bean post processor should return proxy instead of original bean"
        )
    }

    @Test
    fun `should return bean when bean doesnt have methods with custom annotation`() {
        // GIVEN
        val notAnnotatedBean = CustomProfilingTestServiceWithoutAnnotationImpl()
        val notAnnotatedBeanName = getRandomString()

        // WHEN
        val proxy = createProxy(notAnnotatedBean, notAnnotatedBeanName)

        // THEN
        assertFalse(
            Proxy.isProxyClass(proxy?.javaClass), "Bean post processor should return original bean instaed of proxy"
        )
    }

    @Test
    fun `should log method execution time when method is annotated with CustomProfiling`() {
        // GIVEN
        val annotatedBean = CustomProfilingTestServiceWithAnnotationImpl()
        val annotatedBeanName = getRandomString()

        val proxy = createProxy(annotatedBean, annotatedBeanName) as CustomProfilingTestService

        // WHEN
        proxy.test(getRandomString())

        // THEN
        assertTrue(testLogAppender.list.isNotEmpty(), "Log event must contain logs")
        assertNotNull(testLogAppender.list.find { it.message.contains(PROFILING_PART_LOG_MESSAGE_TEMPLATE) })
        assertEquals(
            Level.INFO, testLogAppender.list.find {
                it.message.contains(PROFILING_PART_LOG_MESSAGE_TEMPLATE)
            }?.level
        )
    }

    @Test
    fun `should log method execution time when method is annotated with CustomProfiling and throws an error`() {
        // GIVEN
        val annotatedBean = CustomProfilingTestServiceWithAnnotationImpl()
        val annotatedBeanName = getRandomString()

        val proxy = createProxy(annotatedBean, annotatedBeanName) as CustomProfilingTestService

        // WHEN-THEN
        assertThrows<Exception> { proxy.errorTest(getRandomString()) }
        assertTrue(testLogAppender.list.isNotEmpty(), "Log event must contain logs")
        assertNotNull(testLogAppender.list.find { it.message.contains(PROFILING_PART_LOG_MESSAGE_TEMPLATE) })
        assertEquals(Level.INFO, testLogAppender.list.find {
            it.message.contains(PROFILING_PART_LOG_MESSAGE_TEMPLATE)
        }?.level)
    }

    @Test
    fun `should not log when method is not annotated and class doesnt have annotated method too`() {
        // GIVEN
        val notAnnotatedBean = CustomProfilingTestServiceWithoutAnnotationImpl()
        val notAnnotatedBeanName = getRandomString()

        val proxy = createProxy(notAnnotatedBean, notAnnotatedBeanName) as CustomProfilingTestService

        // WHEN
        proxy.test(getRandomString())

        // THEN
        assertTrue(testLogAppender.list.isEmpty(), "Log event found in NOT annotated method")
    }

    @Test
    fun `should not log method execution time when method is not annotated, but class have annotated method`() {
        // GIVEN
        val notAnnotatedBean = CustomProfilingTestServiceWithAnnotationImpl()
        val notAnnotatedBeanName = getRandomString()

        val proxy = createProxy(notAnnotatedBean, notAnnotatedBeanName) as CustomProfilingTestService

        // WHEN
        proxy.test(100)

        // THEN
        assertTrue(testLogAppender.list.isEmpty(), "Log event found in NOT annotated method")
    }

    private fun createProxy(bean: Any, beanName: String): Any? {
        beanPostProcessor.postProcessBeforeInitialization(bean, beanName)
        return beanPostProcessor.postProcessAfterInitialization(bean, beanName)
    }

    companion object {
        private const val PROFILING_PART_LOG_MESSAGE_TEMPLATE = "was executing for"
    }
}
