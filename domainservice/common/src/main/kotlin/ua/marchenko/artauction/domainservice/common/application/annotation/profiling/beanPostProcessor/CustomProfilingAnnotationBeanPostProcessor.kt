package ua.marchenko.artauction.domainservice.common.application.annotation.profiling.beanPostProcessor

import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.TimeUnit
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import ua.marchenko.artauction.domainservice.common.application.annotation.profiling.annotation.CustomProfiling

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class CustomProfilingAnnotationBeanPostProcessor : BeanPostProcessor {

    private val annotatedMethods: MutableMap<String, List<Method>> = mutableMapOf()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        val annotatedBeanClass = bean.javaClass
        val annotatedBeanClassMethods: List<Method> =
            annotatedBeanClass.methods.filter { it.isAnnotationPresent(CustomProfiling::class.java) }

        if (annotatedBeanClassMethods.isNotEmpty()) {
            annotatedMethods[beanName] = annotatedBeanClassMethods
        }
        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        val methods = annotatedMethods[beanName] ?: return bean
        return Proxy.newProxyInstance(bean.javaClass.classLoader, bean.javaClass.interfaces) { _, method, args ->
            methods.find { originalMethod ->
                areMethodsEquivalent(originalMethod, method)
            }?.let { originalMethod ->
                profileMethodInvocation(bean, method, originalMethod, args)
            } ?: run {
                @Suppress("SpreadOperator")
                method.invoke(bean, *(args.orEmpty()))
            }
        }
    }

    private fun profileMethodInvocation(
        bean: Any,
        methodToCall: Method,
        originalMethod: Method,
        args: Array<Any>?
    ): Any? {
        val timeUnit = originalMethod.getAnnotation(CustomProfiling::class.java).timeUnit
        val beforeTime = System.nanoTime()
        try {
            @Suppress("SpreadOperator")
            return methodToCall.invoke(bean, *(args.orEmpty()))
        } finally {
            val methodTime = timeUnit.convert((System.nanoTime() - beforeTime), TimeUnit.NANOSECONDS)
            log.info("Method {} was executing for {} {}", methodToCall.name, methodTime, timeUnit)
        }
    }

    private fun areMethodsEquivalent(method1: Method, method2: Method): Boolean {
        return method1.name == method2.name &&
                method1.parameters.map { it.type } == method2.parameters.map { it.type } &&
                method1.parameters.map { it.name } == method2.parameters.map { it.name }
    }

    companion object {
        private val log = LoggerFactory.getLogger(CustomProfilingAnnotationBeanPostProcessor::class.java)
    }
}
