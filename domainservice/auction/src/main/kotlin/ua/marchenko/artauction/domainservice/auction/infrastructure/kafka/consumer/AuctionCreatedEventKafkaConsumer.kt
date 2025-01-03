package ua.marchenko.artauction.domainservice.auction.infrastructure.kafka.consumer

import com.google.protobuf.Parser
import java.time.Clock
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.kafka.handler.KafkaEvent
import systems.ajax.kafka.handler.KafkaHandler
import systems.ajax.kafka.handler.subscription.topic.TopicSingle
import ua.marchenko.artauction.domainservice.auction.infrastructure.kafka.event.AuctionCreatedEvent
import ua.marchenko.artauction.domainservice.auction.infrastructure.kafka.mapper.toAuctionCreatedEvent
import ua.marchenko.internal.KafkaTopic
import ua.marchenko.internal.output.pubsub.auction.AuctionCreatedEvent as AuctionCreatedEventProto

@Component
class AuctionCreatedEventKafkaConsumer(
    private val clock: Clock,
) : KafkaHandler<AuctionCreatedEventProto, TopicSingle> {

    override val groupId: String = CREATED_AUCTION_GROUP_ID

    override val parser: Parser<AuctionCreatedEventProto> = AuctionCreatedEventProto.parser()

    override val subscriptionTopics: TopicSingle = TopicSingle(KafkaTopic.AuctionKafkaTopic.CREATED)

    override fun handle(kafkaEvent: KafkaEvent<AuctionCreatedEventProto>): Mono<Unit> {
        return Mono.just(KafkaEvent)
            .flatMap { processAuctionEvent(kafkaEvent.data.toAuctionCreatedEvent(clock)).toMono() }
            .doFinally { kafkaEvent.ack() }
    }

    private fun processAuctionEvent(event: AuctionCreatedEvent) {
        log.info("Received event: $event")
    }

    companion object {
        private const val CREATED_AUCTION_GROUP_ID = "auctionCreatedConsumerGroup"
    }
}
