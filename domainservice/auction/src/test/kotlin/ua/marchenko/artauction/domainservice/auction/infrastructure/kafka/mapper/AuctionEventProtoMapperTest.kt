package ua.marchenko.artauction.domainservice.auction.infrastructure.kafka.mapper

import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import ua.marchenko.artauction.domainservice.auction.domain.Auction
import ua.marchenko.artauction.domainservice.auction.domain.random
import ua.marchenko.artauction.domainservice.auction.infrastructure.common.mapper.toAuctionProto
import ua.marchenko.artauction.domainservice.auction.infrastructure.common.mapper.toTimestampProto
import ua.marchenko.internal.output.pubsub.auction.AuctionCreatedEvent

class AuctionEventProtoMapperTest {

    @Test
    fun `should return AuctionCreatedEventProto from Auction`() {
        // GIVEN
        val fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
        val auction = Auction.random()
        val expectedAuctionCreatedEventProto = AuctionCreatedEvent.newBuilder().also {
            it.auction = auction.toAuctionProto(fixedClock)
            it.timestamp = LocalDateTime.now(fixedClock).toTimestampProto(fixedClock)
        }.build()

        // WHEN
        val result = auction.toAuctionCreatedEventProto(fixedClock)

        // THEN
        assertEquals(expectedAuctionCreatedEventProto, result)
    }
}
