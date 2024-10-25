package ua.marchenko.artauction.common.nats

import com.google.protobuf.GeneratedMessage
import io.nats.client.Dispatcher
import io.nats.client.MessageHandler
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toMono

@Component
class NatsControllerBeanPostProcessor(private val dispatcher: Dispatcher) : BeanPostProcessor {

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        if (bean is NatsController<*, *>) {
            initializeNatsController(bean)
        }
        return bean
    }

    private fun <RequestT : GeneratedMessage, ResponseT : GeneratedMessage>
            initializeNatsController(controller: NatsController<RequestT, ResponseT>) {
        val messageHandler = MessageHandler { message ->
            Mono.fromCallable { controller.parser.parseFrom(message.data) }
                .flatMap { parsedData ->
                    controller.handle(parsedData)
                }
                .flatMap { parsedData ->
                    Mono.error<ResponseT>(RuntimeException("Test Exception"))
                }
                .onErrorResume {
                    log.error("Error:", it)
                    onParsingError(it, controller.responseType)
                }
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe { response ->
                    controller.connection.publish(message.replyTo, response.toByteArray())
                }
        }
        dispatcher.subscribe(controller.subject, controller.queueGroup, messageHandler)
    }

    private fun <ResponseT : GeneratedMessage> onParsingError(
        throwable: Throwable,
        responseType: ResponseT
    ): Mono<ResponseT> {
        val failureDescriptor = responseType.descriptorForType.findFieldByName(FAILURE)
        val messageDescriptor = failureDescriptor.messageType.findFieldByName(MESSAGE_FIELD)
        val response = responseType.toBuilder().run {
            val failure = newBuilderForField(failureDescriptor)
                .setField(messageDescriptor, throwable.message.orEmpty())
                .build()
            setField(failureDescriptor, failure)
        }.build()
        return (response as? ResponseT)?.toMono() ?: throwable.toMono()
    }

    companion object {
        private val log = LoggerFactory.getLogger(NatsControllerBeanPostProcessor::class.java)
        private const val FAILURE = "failure"
        private const val MESSAGE_FIELD = "message"
    }
}
