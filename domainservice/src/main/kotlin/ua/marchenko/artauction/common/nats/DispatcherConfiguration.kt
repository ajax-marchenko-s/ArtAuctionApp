package ua.marchenko.artauction.common.nats

import io.nats.client.Connection
import io.nats.client.Dispatcher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DispatcherConfiguration(private val connection: Connection) {
    @Bean
    fun dispatcher(): Dispatcher = connection.createDispatcher()
}