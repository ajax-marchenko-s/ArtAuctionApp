package ua.marchenko.artauction.config.annotation

import config.annotation.customProfiling.CustomProfilingTestServiceWithAnnotationImpl
import config.annotation.customProfiling.CustomProfilingTestServiceWithoutAnnotationImpl
import getRandomString
import io.mockk.impl.annotations.InjectMockKs
import java.lang.reflect.Proxy
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test
import org.junit.jupiter.api.Assertions.assertFalse
import ua.marchenko.artauction.common.annotation.profiling.beanPostProcessor.CustomProfilingAnnotationBeanPostProcessor

class CustomProfilingAnnotationBeanPostProcessorTest {

    @InjectMockKs
    private lateinit var beanPostProcessor: CustomProfilingAnnotationBeanPostProcessor

    @Test
    fun `should return proxy of bean when bean has methods with custom annotation`() {
        //GIVEN
        val annotatedBean = CustomProfilingTestServiceWithAnnotationImpl()
        val annotatedBeanName = getRandomString(10)

        //WHEN
        beanPostProcessor.postProcessBeforeInitialization(annotatedBean, annotatedBeanName)
        val proxy = beanPostProcessor.postProcessAfterInitialization(annotatedBean, annotatedBeanName)

        //THEN
        assertTrue(Proxy.isProxyClass(proxy?.javaClass))
    }

    @Test
    fun `should return bean when bean doesnt have methods with custom annotation`() {
        //GIVEN
        val annotatedBean = CustomProfilingTestServiceWithoutAnnotationImpl()
        val annotatedBeanName = getRandomString(10)

        //WHEN
        beanPostProcessor.postProcessBeforeInitialization(annotatedBean, annotatedBeanName)
        val proxy = beanPostProcessor.postProcessAfterInitialization(annotatedBean, annotatedBeanName)

        //THEN
        assertFalse(Proxy.isProxyClass(proxy?.javaClass))//mesaage
    }
}
