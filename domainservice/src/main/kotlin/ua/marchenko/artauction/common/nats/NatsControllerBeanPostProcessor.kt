package ua.marchenko.artauction.common.nats

import com.google.protobuf.GeneratedMessage
import io.nats.client.Dispatcher
import io.nats.client.MessageHandler
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Component
class NatsControllerBeanPostProcessor(private val dispatcher: Dispatcher) : BeanPostProcessor {

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        if (bean is NatsController<*, *>) {
            initializeNatsController(bean)
        }
        return bean
    }

    fun <RequestT : GeneratedMessage, ResponseT : GeneratedMessage>
            initializeNatsController(controller: NatsController<RequestT, ResponseT>) {
        val messageHandler = MessageHandler { message ->
            Mono.fromCallable { controller.parser.parseFrom(message.data) }
                .flatMap { parsedData ->
                    controller.handle(parsedData)
                }
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe { response ->
                    controller.connection.publish(message.replyTo, response.toByteArray())
                }
        }
        dispatcher.subscribe(controller.subject, controller.queueGroup, messageHandler)
    }
}
