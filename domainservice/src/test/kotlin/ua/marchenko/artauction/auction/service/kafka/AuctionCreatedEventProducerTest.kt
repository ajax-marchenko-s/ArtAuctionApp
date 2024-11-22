package ua.marchenko.artauction.auction.service.kafka

import ua.marchenko.artauction.artwork.random
import ua.marchenko.artauction.auction.random
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.parallel.ResourceLock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import systems.ajax.kafka.mock.KafkaMockExtension
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.service.ArtworkService
import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.service.AuctionService
import ua.marchenko.artauction.common.AbstractBaseIntegrationTest
import ua.marchenko.artauction.common.KafkaTestConfiguration
import ua.marchenko.internal.output.pubsub.auction.AuctionCreatedEvent as AuctionCreatedEventProto
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.artauction.user.service.UserService
import ua.marchenko.internal.KafkaTopic
import ua.marchenko.artauction.user.random

@Import(KafkaTestConfiguration::class)
@ResourceLock(KafkaTopic.AuctionKafkaTopic.CREATED)
class AuctionCreatedEventProducerTest : AbstractBaseIntegrationTest {

    @Autowired
    private lateinit var artworkService: ArtworkService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var auctionService: AuctionService

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
        val receivedEvent = testConsumer.awaitFirst({
            it.auction.artworkId == createAuctionRequest.artworkId
        })
        assertNotNull(receivedEvent, "Received event should be not null")
    }

    companion object {
        @JvmField
        @RegisterExtension
        val kafkaMockExtension: KafkaMockExtension = KafkaMockExtension()
    }
}
