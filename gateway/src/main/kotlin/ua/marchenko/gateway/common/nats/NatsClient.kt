package ua.marchenko.gateway.common.nats

import com.google.protobuf.GeneratedMessage
import com.google.protobuf.Parser
import io.nats.client.Connection
import io.nats.client.Dispatcher
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import ua.marchenko.internal.NatsSubject
import ua.marchenko.commonmodels.auction.Auction as AuctionProto

@Component
class NatsClient(
    private val natsConnection: Connection,
    private val dispatcher: Dispatcher,
) {

    fun <Request : GeneratedMessage, Response : GeneratedMessage> doRequest(
        subject: String,
        payload: Request,
        parser: Parser<Response>,
    ): Mono<Response> {
        return Mono.fromFuture { natsConnection.request(subject, payload.toByteArray()) }
            .map { parser.parseFrom(it.data) }
    }

    fun subscribeToCreatedAuction(): Flux<AuctionProto> {
        val sink = Sinks.many().unicast().onBackpressureBuffer<AuctionProto>()
        val subscription = dispatcher.subscribe(NatsSubject.Auction.CREATED_EVENT) { message ->
            runCatching { AuctionProto.parseFrom(message.data) }
                .onSuccess { auction -> sink.tryEmitNext(auction) }
                .onFailure { error -> log.error("Error parsing AuctionProto: ${error.message}", error) }
        }
        return sink.asFlux()
            .doFinally {
                log.info("Unsubscribe from NATS Subject {}", NatsSubject.Auction.CREATED_EVENT)
                dispatcher.unsubscribe(subscription)
            }
    }

    companion object {
        private val log = LoggerFactory.getLogger(NatsClient::class.java)
    }
}
