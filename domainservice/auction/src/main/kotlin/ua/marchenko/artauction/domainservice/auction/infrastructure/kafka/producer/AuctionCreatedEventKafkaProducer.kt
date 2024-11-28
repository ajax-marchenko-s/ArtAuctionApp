package ua.marchenko.artauction.domainservice.auction.infrastructure.kafka.producer

import java.time.Clock
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.kafka.publisher.KafkaPublisher
import ua.marchenko.artauction.domainservice.auction.application.port.output.AuctionCreatedEventProducerOutputPort
import ua.marchenko.artauction.domainservice.auction.domain.Auction
import ua.marchenko.artauction.domainservice.auction.infrastructure.kafka.mapper.toAuctionCreatedEventProto
import ua.marchenko.internal.KafkaTopic

@Component
class AuctionCreatedEventKafkaProducer(
    private val kafkaPublisher: KafkaPublisher,
    private val clock: Clock,
) : AuctionCreatedEventProducerOutputPort {

    override fun sendCreateAuctionEvent(auction: Auction): Mono<Unit> {
        val auctionEvent = auction.toAuctionCreatedEventProto(clock)
        return kafkaPublisher.publish(
            topic = KafkaTopic.AuctionKafkaTopic.CREATED,
            key = auctionEvent.auction.artworkId,
            value = auctionEvent.toByteArray()
        ).doOnError { log.error("Error sending message to Kafka", it) }
            .then(Unit.toMono())
    }

    companion object {
        private val log = LoggerFactory.getLogger(AuctionCreatedEventKafkaProducer::class.java)
    }
}
