package ua.marchenko.artauction.common.annotation.scheduled.beanPostProcessor

import java.lang.reflect.Method
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import ua.marchenko.artauction.common.annotation.scheduled.annotation.CustomScheduled
import ua.marchenko.artauction.common.annotation.scheduled.cron.DayTimeDetails

@Component
class CustomScheduledAnnotationBeanPostProcessor : BeanPostProcessor {

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        val annotatedBeanClassMethods: List<Method> =
            bean.javaClass.methods.filter { it.isAnnotationPresent(CustomScheduled::class.java) }

        val scheduledService: ScheduledExecutorService = Executors.newScheduledThreadPool(CORE_POOL_SIZE)

        annotatedBeanClassMethods.forEach { method ->
            val dayTime = DayTimeDetails.parseTimeString(method.getAnnotation(CustomScheduled::class.java).schedule)
            val initialDelay = dayTime?.calculateTimeDifference(LocalDateTime.now())?.seconds ?: 0L
            val delay = dayTime?.calculateDurationBetween()?.seconds ?: Duration.ofDays(1).seconds
            scheduledService.scheduleWithFixedDelay({ method.invoke(bean) }, initialDelay, delay, TimeUnit.SECONDS)
        }
        return bean
    }

    companion object {
        private const val CORE_POOL_SIZE = 10
    }
}
