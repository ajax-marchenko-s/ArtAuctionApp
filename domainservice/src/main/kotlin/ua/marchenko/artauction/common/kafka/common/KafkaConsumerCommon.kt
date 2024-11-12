package ua.marchenko.artauction.common.kafka.common

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions

fun createBasicKafkaConsumer(
    kafkaProperties: KafkaProperties,
    topics: Set<String>,
    consumerGroupId: String,
): KafkaReceiver<String, ByteArray> {
    val properties = kafkaProperties.consumer.buildProperties(null)

    properties.putAll(
        mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to ByteArrayDeserializer::class.java,
            ConsumerConfig.GROUP_ID_CONFIG to consumerGroupId
        )
    )
    val receiverOptions = ReceiverOptions.create<String, ByteArray>(properties)
        .subscription(topics)
    return KafkaReceiver.create(receiverOptions)
}
