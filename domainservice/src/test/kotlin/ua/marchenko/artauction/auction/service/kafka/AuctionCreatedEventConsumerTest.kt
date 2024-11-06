package ua.marchenko.artauction.auction.service.kafka

import artwork.random
import auction.random
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import java.time.Duration
import org.awaitility.Awaitility.await
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import reactor.kafka.receiver.KafkaReceiver
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.service.ArtworkService
import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.service.AuctionService
import ua.marchenko.artauction.common.AbstractBaseIntegrationTest
import ua.marchenko.artauction.common.kafka.common.createBasicKafkaConsumer
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.artauction.user.service.UserService
import ua.marchenko.internal.KafkaTopic
import user.random

@SpringBootTest
@ActiveProfiles("test")
@Import(AuctionCreatedEventConsumerTest.AuctionCreatedEventConsumerTestConfiguration::class)
class AuctionCreatedEventConsumerTest : AbstractBaseIntegrationTest {

    @Autowired
    private lateinit var artworkService: ArtworkService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var auctionService: AuctionService

    @Autowired
    private lateinit var auctionCreatedEventKafkaConsumerTest: AuctionCreatedEventKafkaConsumer

    private lateinit var testLogAppender: ListAppender<ILoggingEvent>

    @BeforeEach
    fun setup() {
        val logger = LoggerFactory.getLogger(AuctionCreatedEventKafkaConsumer::class.java) as Logger
        testLogAppender = ListAppender()
        testLogAppender.start()
        logger.addAppender(testLogAppender)
    }

    @Test
    fun `should log auction create event received from kafka`() {
        // GIVEN
        val user = userService.save(MongoUser.random(id = null)).block()!!
        val artwork =
            artworkService.save(MongoArtwork.random(id = null, artistId = user.id!!.toHexString())).block()!!
        val createAuctionRequest = CreateAuctionRequest.random(artworkId = artwork.id!!.toHexString())

        auctionCreatedEventKafkaConsumerTest.listenToAuctionNotificationTopic()

        // WHEN
        auctionService.save(createAuctionRequest).subscribe()

        // THEN
        await()
            .atMost(Duration.ofSeconds(15))
            .untilAsserted {
                assertTrue(
                    testLogAppender.list.any {
                        it.formattedMessage.contains(PART_LOG_MESSAGE_TEMPLATE) &&
                                it.formattedMessage.contains("artworkId=${artwork.id}")
                    },
                    "Event in log for creating $createAuctionRequest NOT found, but found ${testLogAppender.list}"
                )
            }
    }

    class AuctionCreatedEventConsumerTestConfiguration(
        private val kafkaProperties: KafkaProperties
    ) {

        @Bean
        fun kafkaReceiverAuctionCreatedEventConsumerTest(): KafkaReceiver<String, ByteArray> {
            return createBasicKafkaConsumer(kafkaProperties, setOf(KafkaTopic.AuctionKafkaTopic.CREATED), GROUP_ID)
        }

        @Bean
        fun auctionCreatedEventKafkaConsumerTest(
            kafkaReceiverAuctionCreatedEventConsumerTest: KafkaReceiver<String, ByteArray>
        ): AuctionCreatedEventKafkaConsumer {
            return AuctionCreatedEventKafkaConsumer(kafkaReceiverAuctionCreatedEventConsumerTest)
        }
    }

    companion object {
        private const val GROUP_ID = "AuctionCreatedEventConsumerTestGroup"
        private const val PART_LOG_MESSAGE_TEMPLATE = "Received event: AuctionCreatedEvent"
    }
}
