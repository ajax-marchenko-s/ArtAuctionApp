package ua.marchenko.artauction.common.annotation.scheduled.contextListener

import java.lang.reflect.Method
import java.time.LocalDateTime
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import ua.marchenko.artauction.common.annotation.scheduled.annotation.CustomScheduled
import ua.marchenko.artauction.common.annotation.scheduled.scheduledDetails.DayTimeDetails

@Component
@Order
class CustomScheduledInvokerContextListener(private val beanFactory: ConfigurableListableBeanFactory) :
    ApplicationListener<ContextRefreshedEvent> {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {

        val scheduledService: ScheduledExecutorService = Executors.newScheduledThreadPool(CORE_POOL_SIZE)

        val context = event.applicationContext
        val beanNames = context.beanDefinitionNames
        for (beanName in beanNames) {
            val beanDefinition = beanFactory.getBeanDefinition(beanName)
            val originalClassName = beanDefinition.beanClassName ?: continue
            val originalClass = Class.forName(originalClassName)
            for (method in originalClass.methods) {
                if (method.isAnnotationPresent(CustomScheduled::class.java)) {
                    val currentBean = context.getBean(beanName)
                    scheduledMethodInvocation(currentBean, method, scheduledService)
                }
            }
        }
    }

    private fun scheduledMethodInvocation(
        bean: Any,
        methodToSchedule: Method,
        scheduledService: ScheduledExecutorService
    ) {
        val annotation = methodToSchedule.getAnnotation(CustomScheduled::class.java)
        @Suppress("SpreadOperator")
        val currentMethod = bean.javaClass.getMethod(methodToSchedule.name, *methodToSchedule.parameterTypes)

        val dayTime =
            DayTimeDetails(annotation.day, annotation.hours, annotation.minutes, annotation.seconds)
        val initialDelay = dayTime.calculateTimeDifference(LocalDateTime.now()).seconds
        val delay = dayTime.calculateDurationBetween().seconds

        scheduledService.scheduleWithFixedDelay(
            { currentMethod.invoke(bean) },
            initialDelay,
            delay,
            TimeUnit.SECONDS
        )

        log.info(
            "Method {} has added to ScheduledExecutorService with delay {} s and initial delay {} s",
            currentMethod.name,
            delay,
            initialDelay
        )
    }

    companion object {
        private const val CORE_POOL_SIZE = 5
        private val log = LoggerFactory.getLogger(CustomScheduledInvokerContextListener::class.java)
    }
}
