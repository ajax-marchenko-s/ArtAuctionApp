package ua.marchenko.artauction.domainservice.auction.infrastructure.kafka.consumer

import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import systems.ajax.kafka.handler.KafkaEvent
import systems.ajax.kafka.handler.KafkaHandler
import systems.ajax.kafka.handler.subscription.topic.TopicSingle
import systems.ajax.nats.publisher.api.NatsMessagePublisher
import ua.marchenko.internal.KafkaTopic
import ua.marchenko.internal.output.pubsub.auction.AuctionCreatedEvent as AuctionCreatedEventProto
import ua.marchenko.internal.NatsSubject

@Component
class AuctionCreatedEventNatsKafkaConsumer(
    private val natsPublisher: NatsMessagePublisher,
) : KafkaHandler<AuctionCreatedEventProto, TopicSingle> {

    override val groupId: String = CREATED_NATS_GROUP_ID

    override val parser: Parser<AuctionCreatedEventProto> = AuctionCreatedEventProto.parser()

    override val subscriptionTopics: TopicSingle = TopicSingle(KafkaTopic.AuctionKafkaTopic.CREATED)

    override fun handle(kafkaEvent: KafkaEvent<AuctionCreatedEventProto>): Mono<Unit> {
        return natsPublisher.publish(
            subject = NatsSubject.Auction.CREATED_EVENT,
            payload = kafkaEvent.data.auction,
        ).doFinally { kafkaEvent.ack() }
    }

    companion object {
        private const val CREATED_NATS_GROUP_ID = "auctionCreatedNatsConsumerGroup"
    }
}
