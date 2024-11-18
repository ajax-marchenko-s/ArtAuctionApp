package ua.marchenko.artauction.auction.mapper

import ua.marchenko.artauction.auction.random
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.internal.output.pubsub.auction.AuctionCreatedEvent as AuctionCreatedEventProto

class AuctionEventProtoMapperTest {

    @Test
    fun `should return AuctionCreatedEventProto when MongoAuction has all non-null properties`() {
        // GIVEN
        val fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
        val mongoAuction = MongoAuction.random()
        val expectedAuctionCreatedEventProto = AuctionCreatedEventProto.newBuilder().also {
            it.auction = mongoAuction.toAuctionProto(fixedClock)
            it.timestamp = LocalDateTime.now(fixedClock).toTimestampProto(fixedClock)
        }.build()

        // WHEN
        val result = mongoAuction.toAuctionCreatedEventProto(fixedClock)

        // THEN
        assertEquals(expectedAuctionCreatedEventProto, result)
    }
}
