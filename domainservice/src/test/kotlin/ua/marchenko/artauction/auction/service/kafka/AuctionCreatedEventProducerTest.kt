package ua.marchenko.artauction.auction.service.kafka

import artwork.random
import auction.random
import java.time.Duration
import kotlin.test.assertTrue
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import reactor.core.scheduler.Schedulers
import reactor.kafka.receiver.KafkaReceiver
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.service.ArtworkService
import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.domain.AuctionCreatedEvent
import ua.marchenko.artauction.auction.mapper.toAuctionCreatedEvent
import ua.marchenko.artauction.auction.mapper.toMongo
import ua.marchenko.artauction.auction.service.AuctionService
import ua.marchenko.artauction.common.AbstractBaseIntegrationTest
import ua.marchenko.internal.output.pubsub.auction.AuctionCreatedEvent as AuctionCreatedEventProto
import ua.marchenko.artauction.common.kafka.common.createBasicKafkaConsumer
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.artauction.user.service.UserService
import ua.marchenko.internal.KafkaTopic
import user.random

@Import(AuctionCreatedEventProducerTest.AuctionCreatedEventProducerTestConfiguration::class)
class AuctionCreatedEventProducerTest : AbstractBaseIntegrationTest {

    @Autowired
    private lateinit var artworkService: ArtworkService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var auctionService: AuctionService

    @Autowired
    private lateinit var kafkaReceiverAuctionCreatedEventProducerTest: KafkaReceiver<String, ByteArray>

    @Test
    fun `should send message to CreatedAuction topic when creating auction`() {
        // GIVEN
        val receivedMessages = mutableListOf<AuctionCreatedEvent>()
        val user = userService.save(MongoUser.random(id = null)).block()!!
        val artwork =
            artworkService.save(MongoArtwork.random(id = null, artistId = user.id!!.toHexString())).block()!!
        val createAuctionRequest = CreateAuctionRequest.random(artworkId = artwork.id!!.toHexString())

        kafkaReceiverAuctionCreatedEventProducerTest.receive()
            .doOnNext { record ->
                receivedMessages.add(AuctionCreatedEventProto.parseFrom(record.value()).toAuctionCreatedEvent())
            }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()

        // WHEN
        auctionService.save(createAuctionRequest).subscribe()

        // THEN
        await().atMost(Duration.ofSeconds(15))
            .untilAsserted {
                assertTrue(
                    receivedMessages.any { it.auction.copy(id = null) == createAuctionRequest.toMongo() },
                    "ReceivedMessages should contains event of Auction of request $createAuctionRequest, " +
                            "but contains $receivedMessages"
                )
            }
    }

    class AuctionCreatedEventProducerTestConfiguration(
        private val kafkaProperties: KafkaProperties
    ) {
        @Bean
        fun kafkaReceiverAuctionCreatedEventProducerTest(): KafkaReceiver<String, ByteArray> {
            return createBasicKafkaConsumer(kafkaProperties, setOf(KafkaTopic.AuctionKafkaTopic.CREATED), GROUP_ID)
        }
    }

    companion object {
        private const val GROUP_ID = "AuctionCreatedEventProducerTestGroup"
    }
}
