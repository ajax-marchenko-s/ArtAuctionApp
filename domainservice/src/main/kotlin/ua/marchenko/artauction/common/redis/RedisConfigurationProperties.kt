package ua.marchenko.artauction.common.redis

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.redis")
data class RedisConfigurationProperties(
    val host: String,
    val port: Int,
    val timeout: Long,
    val database: Int,
)
