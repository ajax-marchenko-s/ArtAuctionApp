package ua.marchenko.gateway.common.nats

import com.google.protobuf.GeneratedMessage
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class NatsClient(private val natsConnection: Connection) {
    fun <Request : GeneratedMessage, Response : GeneratedMessage> doRequest(
        subject: String,
        payload: Request,
        parser: Parser<Response>,
    ): Mono<Response> {
        return Mono.fromFuture { natsConnection.request(subject, payload.toByteArray()) }
            .map { parser.parseFrom(it.data) }
    }
}
