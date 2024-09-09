package ua.marchenko.artauction.common.annotation.scheduled.contextListener

import java.lang.reflect.Method
import java.time.LocalDateTime
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import ua.marchenko.artauction.common.annotation.scheduled.annotation.CustomScheduled
import ua.marchenko.artauction.common.annotation.scheduled.scheduledDetails.DayTimeDetails

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
class CustomScheduledInvokerContextListener(private val beanFactory: ConfigurableListableBeanFactory) :
    ApplicationListener<ContextRefreshedEvent>, DisposableBean {

    val scheduledService: ScheduledExecutorService = Executors.newScheduledThreadPool(CORE_POOL_SIZE)

    override fun onApplicationEvent(event: ContextRefreshedEvent) {

        val context = event.applicationContext
        val beanNames = context.beanDefinitionNames
        for (beanName in beanNames) {
            val beanDefinition = beanFactory.getBeanDefinition(beanName)
            val originalClassName = beanDefinition.beanClassName ?: continue
            val originalClass = Class.forName(originalClassName)
            for (originalMethod in originalClass.methods) {
                if (originalMethod.isAnnotationPresent(CustomScheduled::class.java)) {
                    val currentBean = context.getBean(beanName)
                    scheduledMethodInvocation(currentBean, originalMethod)
                }
            }
        }
    }

    private fun scheduledMethodInvocation(
        bean: Any,
        originalMethod: Method
    ) {
        @Suppress("SpreadOperator")
        val currentMethod = bean.javaClass.getMethod(originalMethod.name, *originalMethod.parameterTypes)
        val annotation = originalMethod.getAnnotation(CustomScheduled::class.java)
        val dayTime = DayTimeDetails(annotation.day, annotation.hours, annotation.minutes, annotation.seconds)
        val initialDelay = dayTime.calculateTimeDifference(LocalDateTime.now().minusSeconds(1)).seconds
        val delay = dayTime.calculateDurationBetween().seconds
        scheduledService.scheduleWithFixedDelay({ currentMethod.invoke(bean) }, initialDelay, delay, TimeUnit.SECONDS)
        log.info("Method {} has added to ScheduledExecutorService with delay {} s", currentMethod.name, delay)
    }

    override fun destroy() {
        log.info("Shutting down CustomScheduledInvokerContextListener")
        scheduledService.shutdown()
    }

    companion object {
        private const val CORE_POOL_SIZE = 5
        private val log = LoggerFactory.getLogger(CustomScheduledInvokerContextListener::class.java)
    }
}
