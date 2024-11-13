package auction

import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.random.Random
import org.bson.types.ObjectId
import ua.marchenko.artauction.auction.mapper.toBigDecimalProto
import ua.marchenko.artauction.auction.mapper.toTimestampProto
import ua.marchenko.commonmodels.auction.Auction as AuctionProto
import ua.marchenko.commonmodels.auction.Auction.Bid as BidProto

object AuctionProtoFixture {

    fun randomAuctionProto(): AuctionProto {
        val fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
        return AuctionProto.newBuilder().also {
            it.id = ObjectId().toHexString()
            it.artworkId = ObjectId().toHexString()
            it.startBid = Random.nextDouble(10.0, 100.0).toBigDecimal().toBigDecimalProto()
            it.startedAt = LocalDateTime.now().toTimestampProto(fixedClock)
            it.finishedAt = LocalDateTime.now().toTimestampProto(fixedClock)
            it.addAllBuyers(listOf(randomBidProto(), randomBidProto(), randomBidProto()))
        }.build()
    }

    fun randomBidProto(): BidProto = BidProto.newBuilder().also {
        it.bid = Random.nextDouble(10.0, 100.0).toBigDecimal().toBigDecimalProto()
        it.buyerId = ObjectId().toHexString()
    }.build()
}
