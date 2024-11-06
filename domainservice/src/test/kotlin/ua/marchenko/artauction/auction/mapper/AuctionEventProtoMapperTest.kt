package ua.marchenko.artauction.auction.mapper

import auction.random
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.internal.output.pubsub.auction.AuctionCreatedEvent as AuctionCreatedEventProto

class AuctionEventProtoMapperTest {

    @Test
    fun `should return AuctionCreatedEventProto when MongoAuction has all non-null properties`() {
        // GIVEN
        val mongoAuction = MongoAuction.random()
        val expectedAuctionCreatedEventProto = AuctionCreatedEventProto.newBuilder().also {
            it.auction = mongoAuction.toAuctionProto()
        }.build()

        // WHEN
        val result = mongoAuction.toAuctionCreatedEventProto()

        // THEN
        assertEquals(expectedAuctionCreatedEventProto.auction, result.auction)
    }
}
