package ua.marchenko.artauction.config.annotation

import config.annotation.customProfiling.CustomProfilingTestServiceWithAnnotationImpl
import config.annotation.customProfiling.CustomProfilingTestServiceWithoutAnnotationImpl
import getRandomString
import java.lang.reflect.Proxy
import ch.qos.logback.classic.Logger
import config.annotation.customProfiling.CustomProfilingTestService
import config.annotation.customProfiling.MockLogger
import io.mockk.impl.annotations.InjectMockKs
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import ua.marchenko.artauction.common.annotation.profiling.beanPostProcessor.CustomProfilingAnnotationBeanPostProcessor

class CustomProfilingAnnotationBeanPostProcessorTest {

    @InjectMockKs
    private lateinit var beanPostProcessor: CustomProfilingAnnotationBeanPostProcessor

    private lateinit var testLogger: MockLogger

    @BeforeEach
    fun setup() {
        testLogger = MockLogger()
        testLogger.start()
        val logger = LoggerFactory.getLogger(CustomProfilingAnnotationBeanPostProcessor::class.java) as Logger
        logger.addAppender(testLogger)
    }

    @Test
    fun `should return proxy of bean when bean has methods with custom annotation`() {
        // GIVEN
        val annotatedBean = CustomProfilingTestServiceWithAnnotationImpl()
        val annotatedBeanName = getRandomString(10)

        // WHEN
        val proxy = createProxy(annotatedBean, annotatedBeanName)

        // THEN
        assertTrue(Proxy.isProxyClass(proxy?.javaClass), "Bean post processor should return proxy instead of original bean")
    }

    @Test
    fun `should return bean when bean doesnt have methods with custom annotation`() {
        // GIVEN
        val notAnnotatedBean = CustomProfilingTestServiceWithoutAnnotationImpl()
        val notAnnotatedBeanName = getRandomString()

        // WHEN
        val proxy = createProxy(notAnnotatedBean, notAnnotatedBeanName)

        // THEN
        assertFalse(Proxy.isProxyClass(proxy?.javaClass), "Bean post processor should return original bean instaed of proxy")
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
        assertEquals(1, testLogger.logs.size, "Log event must contain 1 log")
    }

    @Test
    fun `should log method execution time when method is annotated with CustomProfiling and throws an error`() {
        // GIVEN
        val annotatedBean = CustomProfilingTestServiceWithAnnotationImpl()
        val annotatedBeanName = getRandomString()

        val proxy = createProxy(annotatedBean, annotatedBeanName) as CustomProfilingTestService

        // WHEN-THEN
        assertThrows<Exception> { proxy.errorTest(getRandomString()) }
        assertEquals(1, testLogger.logs.size, "Log event must contain 1 log")
    }

    @Test
    fun `should not log method execution time when method is not annotated and class doesnt have annotated method too`() {
        // GIVEN
        val notAnnotatedBean = CustomProfilingTestServiceWithoutAnnotationImpl()
        val notAnnotatedBeanName = getRandomString()

        val proxy = createProxy(notAnnotatedBean, notAnnotatedBeanName) as CustomProfilingTestService

        // WHEN
        proxy.test(getRandomString())

        // THEN
        assertTrue(testLogger.logs.isEmpty(), "Log event found in NOT annotated method")
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
        assertTrue(testLogger.logs.isEmpty(), "Log event found in NOT annotated method")
    }

    private fun createProxy(bean: Any, beanName: String): Any? {
        beanPostProcessor.postProcessBeforeInitialization(bean, beanName)
        return beanPostProcessor.postProcessAfterInitialization(bean, beanName)
    }
}
