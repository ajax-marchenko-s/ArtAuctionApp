package ua.marchenko.artauction.common.annotation.scheduled.config

import java.time.Clock
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CustomScheduledConfiguration {

    @Bean
    fun scheduledExecutorService(): ScheduledExecutorService {
        return Executors.newScheduledThreadPool(CORE_POOL_SIZE)
    }

    @Bean
    fun clock(): Clock {
        return Clock.systemDefaultZone()
    }

    companion object {
        private const val CORE_POOL_SIZE = 5
    }
}
