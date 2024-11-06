package ua.marchenko.gateway.common.nats

import io.nats.client.Connection
import io.nats.client.Nats
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NatsConnectionConfiguration(@Value("\${nats.uri}") private val natsUri: String) {
    @Bean
    fun connection(): Connection = Nats.connect(natsUri)
}
