package ua.marchenko.artauction.common.kafka.configuration

import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.receiver.KafkaReceiver
import ua.marchenko.artauction.common.kafka.common.createBasicKafkaConsumer
import ua.marchenko.internal.KafkaTopic

@Configuration
class KafkaConsumerConfiguration(private val kafkaProperties: KafkaProperties) {

    @Bean
    fun createdAuctionKafkaConsumer(): KafkaReceiver<String, ByteArray> =
        createBasicKafkaConsumer(kafkaProperties, setOf(KafkaTopic.AuctionKafkaTopic.CREATED), CREATED_GROUP_ID)

    @Bean
    fun createdAuctionForNatsKafkaConsumer(): KafkaReceiver<String, ByteArray> =
        createBasicKafkaConsumer(kafkaProperties, setOf(KafkaTopic.AuctionKafkaTopic.CREATED), CREATED_NATS_GROUP_ID)


    companion object {
        private const val CREATED_GROUP_ID = "auctionCreatedConsumerGroup"
        private const val CREATED_NATS_GROUP_ID = "auctionCreatedNatsConsumerGroup"
    }
}
