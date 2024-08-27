package ua.marchenko.artauction.common.annotation.profiling.beanPostProcessor

import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.TimeUnit
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import ua.marchenko.artauction.common.annotation.profiling.annotation.CustomProfiling
import ua.marchenko.artauction.common.annotation.scheduled.beanPostProcessor.CustomScheduledAnnotationBeanPostProcessor

@Component
class CustomProfilingAnnotationBeanPostProcessor : BeanPostProcessor {

    private val annotatedMethods: MutableMap<String, Map<Method, TimeUnit>> = mutableMapOf()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        val annotatedBeanClass = bean.javaClass
        val annotatedBeanClassMethods: List<Method> =
            annotatedBeanClass.methods.filter { it.isAnnotationPresent(CustomProfiling::class.java) }

        if (annotatedBeanClassMethods.isNotEmpty()) {
            val methodsWithTimeUnits = annotatedBeanClassMethods.associateWith { method ->
                method.getAnnotation(CustomProfiling::class.java).timeUnit
            }
            annotatedMethods[beanName] = methodsWithTimeUnits
        }
        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        val methods = annotatedMethods[beanName] ?: return bean
        return Proxy.newProxyInstance(bean.javaClass.classLoader, bean.javaClass.interfaces) { _, method, args ->
            methods.keys.find { it.name == method.name && it.parameters.contentEquals(method.parameters) }
                ?.let { methodToProfile ->
                    val annotationTimeUnit = methods[methodToProfile] ?: TimeUnit.NANOSECONDS
                    val beforeTime = System.nanoTime()

                    @Suppress("SpreadOperator")
                    val result = method.invoke(bean, *(args ?: emptyArray()))
                    val methodTime = annotationTimeUnit.convert((System.nanoTime() - beforeTime), TimeUnit.NANOSECONDS)
                    log.info("Method {} was executing for {} {}", method.name, methodTime, annotationTimeUnit)
                    result
                } ?: run {
                @Suppress("SpreadOperator")
                method.invoke(bean, *(args ?: emptyArray()))
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(CustomScheduledAnnotationBeanPostProcessor::class.java)
    }
}
