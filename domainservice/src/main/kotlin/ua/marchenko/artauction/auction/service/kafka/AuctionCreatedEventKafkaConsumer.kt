package ua.marchenko.artauction.auction.service.kafka

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.core.scheduler.Schedulers
import reactor.kafka.receiver.KafkaReceiver
import reactor.kotlin.core.publisher.toMono
import ua.marchenko.artauction.auction.domain.AuctionCreatedEvent
import ua.marchenko.artauction.auction.mapper.toAuctionCreatedEvent
import ua.marchenko.internal.output.pubsub.auction.AuctionCreatedEvent as AuctionCreatedEventProto

@Component
class AuctionCreatedEventKafkaConsumer(
    private val createdAuctionKafkaConsumer: KafkaReceiver<String, ByteArray>
) {

    @EventListener(ApplicationReadyEvent::class)
    fun listenToAuctionNotificationTopic() {
        createdAuctionKafkaConsumer.receive()
            .flatMap { record ->
                val event = AuctionCreatedEventProto.parseFrom(record.value()).toAuctionCreatedEvent()
                processAuctionEvent(event).toMono()
                    .doFinally { record.receiverOffset().acknowledge() }
            }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }

    private fun processAuctionEvent(event: AuctionCreatedEvent) {
        log.info("Received event: $event")
    }

    companion object {
        private val log = LoggerFactory.getLogger(AuctionCreatedEventKafkaConsumer::class.java)
    }
}
