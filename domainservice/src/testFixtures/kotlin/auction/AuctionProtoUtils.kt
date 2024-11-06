package auction

import java.time.LocalDateTime
import kotlin.random.Random
import org.bson.types.ObjectId
import ua.marchenko.artauction.auction.mapper.toBigDecimalProto
import ua.marchenko.artauction.auction.mapper.toTimestampProto
import ua.marchenko.internal.commonmodels.auction.Auction as AuctionProto
import ua.marchenko.internal.commonmodels.auction.Auction.Bid as BidProto

object AuctionProtoFixture {

    fun randomAuctionProto(): AuctionProto = AuctionProto.newBuilder().also {
        it.id = ObjectId().toHexString()
        it.artworkId = ObjectId().toHexString()
        it.startBid = Random.nextDouble(10.0, 100.0).toBigDecimal().toBigDecimalProto()
        it.startedAt = LocalDateTime.now().toTimestampProto()
        it.finishedAt = LocalDateTime.now().toTimestampProto()
        it.addAllBuyers(listOf(randomBidProto(), randomBidProto(), randomBidProto()))
    }.build()

    fun randomBidProto(): BidProto = BidProto.newBuilder().also {
        it.bid = Random.nextDouble(10.0, 100.0).toBigDecimal().toBigDecimalProto()
        it.buyerId = ObjectId().toHexString()
    }.build()
}
