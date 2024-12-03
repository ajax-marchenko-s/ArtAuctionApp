package ua.marchenko.artauction.domainservice.auction.infrastructure.kafka.producer

import java.time.Clock
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.parallel.ResourceLock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import systems.ajax.kafka.mock.KafkaMockExtension
import ua.marchenko.artauction.domainservice.artwork.application.port.input.ArtworkServiceInputPort
import ua.marchenko.artauction.domainservice.auction.application.port.input.AuctionServiceInputPort
import ua.marchenko.artauction.domainservice.auction.domain.Auction
import ua.marchenko.artauction.domainservice.auction.domain.random
import ua.marchenko.artauction.domainservice.auction.infrastructure.kafka.mapper.toAuctionCreatedEvent
import ua.marchenko.artauction.domainservice.user.application.port.input.UserServiceInputPort
import ua.marchenko.artauction.domainservice.user.domain.random
import ua.marchenko.artauction.domainservice.artwork.domain.CreateArtwork
import ua.marchenko.artauction.domainservice.artwork.domain.random
import ua.marchenko.artauction.domainservice.auction.domain.CreateAuction
import ua.marchenko.artauction.domainservice.user.domain.CreateUser
import ua.marchenko.artauction.domainservice.utils.AbstractBaseIntegrationTest
import ua.marchenko.artauction.domainservice.utils.KafkaTestConfiguration
import ua.marchenko.internal.KafkaTopic
import ua.marchenko.internal.output.pubsub.auction.AuctionCreatedEvent

@Import(KafkaTestConfiguration::class)
@ResourceLock(KafkaTopic.AuctionKafkaTopic.CREATED)
class AuctionCreatedEventProducerTest : AbstractBaseIntegrationTest {

    @Autowired
    private lateinit var artworkService: ArtworkServiceInputPort

    @Autowired
    private lateinit var userService: UserServiceInputPort

    @Autowired
    private lateinit var auctionService: AuctionServiceInputPort

    @Autowired
    private lateinit var clock: Clock

    @Test
    fun `should send message to CreatedAuction topic when creating auction`() {
        // GIVEN
        val user = userService.save(CreateUser.random()).block()!!
        val artwork =
            artworkService.save(CreateArtwork.random(artistId = user.id)).block()!!
        val createAuction = CreateAuction.random(artworkId = artwork.id)
        val expectedAuction = Auction(
            id = EMPTY_STRING,
            artworkId = createAuction.artworkId,
            startBid = createAuction.startBid,
            buyers = createAuction.buyers,
            startedAt = createAuction.startedAt,
            finishedAt = createAuction.finishedAt
        )

        val testConsumer = kafkaMockExtension.listen<AuctionCreatedEvent>(
            topic = KafkaTopic.AuctionKafkaTopic.CREATED,
            parser = AuctionCreatedEvent.parser()
        )

        // WHEN
        auctionService.save(createAuction).subscribe()

        // THEN
        val receivedEvent: AuctionCreatedEvent = testConsumer.awaitFirst({
            it.auction.artworkId == createAuction.artworkId
        })
        assertEquals(expectedAuction, receivedEvent.toAuctionCreatedEvent(clock).auction.copy(id = EMPTY_STRING))
    }

    companion object {
        @JvmField
        @RegisterExtension
        val kafkaMockExtension: KafkaMockExtension = KafkaMockExtension()
        private const val EMPTY_STRING = ""
    }
}
