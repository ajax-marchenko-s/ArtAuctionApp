package ua.marchenko.artauction.auction.service.kafka

import java.time.Clock
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.parallel.ResourceLock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import systems.ajax.kafka.mock.KafkaMockExtension
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.random
import ua.marchenko.artauction.artwork.service.ArtworkService
import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.mapper.toAuctionCreatedEvent
import ua.marchenko.artauction.auction.mapper.toMongo
import ua.marchenko.artauction.auction.random
import ua.marchenko.artauction.auction.service.AuctionService
import ua.marchenko.artauction.common.AbstractBaseIntegrationTest
import ua.marchenko.artauction.common.KafkaTestConfiguration
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.artauction.user.random
import ua.marchenko.artauction.user.service.UserService
import ua.marchenko.internal.KafkaTopic
import ua.marchenko.internal.output.pubsub.auction.AuctionCreatedEvent as AuctionCreatedEventProto

@Import(KafkaTestConfiguration::class)
@ResourceLock(KafkaTopic.AuctionKafkaTopic.CREATED)
class AuctionCreatedEventProducerTest : AbstractBaseIntegrationTest {

    @Autowired
    private lateinit var artworkService: ArtworkService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var auctionService: AuctionService

    @Autowired
    private lateinit var clock: Clock

    @Test
    fun `should send message to CreatedAuction topic when creating auction`() {
        // GIVEN
        val user = userService.save(MongoUser.random(id = null)).block()!!
        val artwork =
            artworkService.save(MongoArtwork.random(id = null, artistId = user.id!!.toHexString())).block()!!
        val createAuctionRequest = CreateAuctionRequest.random(artworkId = artwork.id!!.toHexString())

        val testConsumer = kafkaMockExtension.listen<AuctionCreatedEventProto>(
            topic = KafkaTopic.AuctionKafkaTopic.CREATED,
            parser = AuctionCreatedEventProto.parser()
        )

        // WHEN
        auctionService.save(createAuctionRequest).subscribe()

        // THEN
        val receivedEvent: AuctionCreatedEventProto = testConsumer.awaitFirst({
            it.auction.artworkId == createAuctionRequest.artworkId
        })
        assertEquals(receivedEvent.toAuctionCreatedEvent(clock).auction.copy(id = null), createAuctionRequest.toMongo())
    }

    companion object {
        @JvmField
        @RegisterExtension
        val kafkaMockExtension: KafkaMockExtension = KafkaMockExtension()
    }
}
