package ua.marchenko.artauction.domainservice.auction.infrastructure.kafka.consumer

import ua.marchenko.artauction.domainservice.auction.application.port.input.AuctionServiceInputPort
import ua.marchenko.artauction.domainservice.user.application.port.input.UserServiceInputPort
import ua.marchenko.artauction.domainservice.artwork.application.port.input.ArtworkServiceInputPort
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
import ua.marchenko.artauction.domainservice.auction.domain.Auction
import ua.marchenko.artauction.domainservice.auction.domain.random
import ua.marchenko.artauction.domainservice.user.domain.User
import ua.marchenko.artauction.domainservice.artwork.domain.CreateArtwork
import ua.marchenko.artauction.domainservice.artwork.domain.random
import ua.marchenko.artauction.domainservice.utils.AbstractBaseIntegrationTest
import ua.marchenko.artauction.domainservice.user.domain.random

class AuctionCreatedEventConsumerTest : AbstractBaseIntegrationTest {

    @Autowired
    private lateinit var artworkService: ArtworkServiceInputPort

    @Autowired
    private lateinit var userService: UserServiceInputPort

    @Autowired
    private lateinit var auctionService: AuctionServiceInputPort

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
        val user = userService.save(User.random(id = null)).block()!!
        val artwork =
            artworkService.save(CreateArtwork.random(artistId = user.id!!)).block()!!
        val auction = Auction.random(id = null, artworkId = artwork.id!!)

        // WHEN
        auctionService.save(auction).subscribe()

        // THEN
        await()
            .atMost(Duration.ofSeconds(15))
            .untilAsserted {
                assertTrue(
                    testLogAppender.list.any {
                        it.formattedMessage.contains(PART_LOG_MESSAGE_TEMPLATE) &&
                                it.formattedMessage.contains("artworkId=${artwork.id}")
                    },
                    "Event in log for creating $auction NOT found, but found ${testLogAppender.list}"
                )
            }
    }

    companion object {
        private const val PART_LOG_MESSAGE_TEMPLATE = "Received event: AuctionCreatedEvent"
    }
}
