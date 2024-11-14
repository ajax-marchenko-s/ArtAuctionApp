package ua.marchenko.artauction.auction.service.kafka

import io.nats.client.Connection
import java.time.Clock
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kafka.receiver.KafkaReceiver
import reactor.kotlin.core.publisher.toMono
import ua.marchenko.artauction.auction.domain.AuctionCreatedEvent
import ua.marchenko.artauction.auction.mapper.toAuctionCreatedEvent
import ua.marchenko.artauction.auction.mapper.toAuctionProto
import ua.marchenko.internal.output.pubsub.auction.AuctionCreatedEvent as AuctionCreatedEventProto
import ua.marchenko.internal.NatsSubject

@Component
class AuctionCreatedEventNatsKafkaConsumer(
    private val createdAuctionForNatsKafkaConsumer: KafkaReceiver<String, ByteArray>,
    private val natsConnection: Connection,
    private val clock: Clock,
) {

    @EventListener(ApplicationReadyEvent::class)
    fun listenToCreatedAuctionTopic() {
        createdAuctionForNatsKafkaConsumer.receive()
            .flatMap { record ->
                Mono.defer {
                    val event = AuctionCreatedEventProto.parseFrom(record.value()).toAuctionCreatedEvent(clock)
                    processAuctionEvent(event).toMono()
                }.onErrorResume { error ->
                    log.error("Error occurred:", error)
                    Mono.empty()
                }.doFinally { record.receiverOffset().acknowledge() }
            }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }

    private fun processAuctionEvent(event: AuctionCreatedEvent) {
        natsConnection.publish(
            NatsSubject.AuctionNatsSubject.CREATED_EVENT,
            event.auction.toAuctionProto(clock).toByteArray()
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(AuctionCreatedEventNatsKafkaConsumer::class.java)
    }
}
