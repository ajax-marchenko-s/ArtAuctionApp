package ua.marchenko.artauction.common.kafka.configuration

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import ua.marchenko.internal.KafkaTopic

@Configuration
class KafkaTopicConfiguration {

    @Bean
    fun auctionNotificationTopic(): NewTopic {
        return TopicBuilder.name(KafkaTopic.AuctionKafkaTopic.CREATED)
            .partitions(AUCTION_NOTIFICATION_TOPIC_PARTITIONS)
            .build()
    }

    companion object {
        const val AUCTION_NOTIFICATION_TOPIC_PARTITIONS = 3
    }
}
