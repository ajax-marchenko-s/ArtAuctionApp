package ua.marchenko.artauction.domainservice.common.infrastructure.redis.configuration

import java.time.Duration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableConfigurationProperties(RedisConfigurationProperties::class)
class RedisConfiguration(
    private val redisProperties: RedisConfigurationProperties,
) {

    @Bean
    fun reactiveRedisTemplate(reactiveRedisConnectionFactory: ReactiveRedisConnectionFactory):
            ReactiveRedisTemplate<String, ByteArray> {
        val serializer = RedisSerializer.byteArray()
        val context = RedisSerializationContext.newSerializationContext<String, ByteArray>(StringRedisSerializer())
            .value(serializer).build()
        return ReactiveRedisTemplate(reactiveRedisConnectionFactory, context)
    }

    @Bean
    fun reactiveRedisConnectionFactory(): ReactiveRedisConnectionFactory {
        val config = RedisStandaloneConfiguration(redisProperties.host, redisProperties.port)
        config.database = redisProperties.database
        return LettuceConnectionFactory(
            config,
            LettuceClientConfiguration.builder().commandTimeout(Duration.ofMillis(redisProperties.timeout)).build()
        )
    }
}
