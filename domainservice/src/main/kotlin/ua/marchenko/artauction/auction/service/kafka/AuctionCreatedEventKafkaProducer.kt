package ua.marchenko.artauction.auction.service.kafka

import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toMono
import ua.marchenko.artauction.auction.mapper.toAuctionCreatedEventProto
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.internal.KafkaTopic

@Component
class AuctionCreatedEventKafkaProducer(private val kafkaSender: KafkaSender<String, ByteArray>) {

    fun sendCreateAuctionEvent(auction: MongoAuction): Mono<Unit> {
        val auctionEvent = auction.toAuctionCreatedEventProto()
        return kafkaSender.send(
            SenderRecord.create(
                ProducerRecord(
                    KafkaTopic.AuctionKafkaTopic.CREATED,
                    auctionEvent.auction.artworkId,
                    auctionEvent.toByteArray()
                ),
                null
            ).toMono()
        )
            .doOnError { error -> println("Error sending message to Kafka: ${error.message}") }
            .then(Unit.toMono())
    }
}
