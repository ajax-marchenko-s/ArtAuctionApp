package ua.marchenko.artauction.auction.service.kafka

import java.time.Clock
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toMono
import ua.marchenko.artauction.auction.mapper.toAuctionCreatedEventProto
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.internal.KafkaTopic

@Component
class AuctionCreatedEventKafkaProducer(
    private val kafkaSender: KafkaSender<String, ByteArray>,
    private val clock: Clock,
) {

    fun sendCreateAuctionEvent(auction: MongoAuction): Mono<Unit> {
        return kafkaSender.send(createAuctionCreatedSenderRecord(auction).toMono())
            .doOnError { log.error("Error sending message to Kafka", it) }
            .then(Unit.toMono())
    }

    private fun createAuctionCreatedSenderRecord(auction: MongoAuction): SenderRecord<String, ByteArray, Void> {
        val auctionEvent = auction.toAuctionCreatedEventProto(clock)
        return SenderRecord.create(
            ProducerRecord(
                KafkaTopic.AuctionKafkaTopic.CREATED,
                auctionEvent.auction.artworkId,
                auctionEvent.toByteArray()
            ),
            null
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(AuctionCreatedEventKafkaProducer::class.java)
    }
}
