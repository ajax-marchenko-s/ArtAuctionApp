package ua.marchenko.artauction.common

import com.google.protobuf.GeneratedMessage
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractBaseNatsControllerTest : AbstractBaseIntegrationTest {

    @Autowired
    private lateinit var natsConnection: Connection

    fun <RequestT : GeneratedMessage, ResponseT : GeneratedMessage> doRequest(
        subject: String,
        request: RequestT,
        parser: Parser<ResponseT>,
    ): ResponseT {
        val response = natsConnection.request(
            subject,
            request.toByteArray()
        )
        return parser.parseFrom(response.get().data)
    }
}
