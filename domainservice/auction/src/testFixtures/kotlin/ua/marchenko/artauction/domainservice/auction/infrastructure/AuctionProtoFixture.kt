package ua.marchenko.artauction.domainservice.auction.infrastructure

import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.random.Random
import org.bson.types.ObjectId
import ua.marchenko.artauction.domainservice.auction.infrastructure.common.mapper.CommonMapper.toBigDecimalProto
import ua.marchenko.artauction.domainservice.auction.infrastructure.common.mapper.CommonMapper.toTimestampProto
import ua.marchenko.commonmodels.auction.Auction
import ua.marchenko.commonmodels.auction.Auction.Bid
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionRequest

object AuctionProtoFixture {

    fun randomAuctionProto(): Auction {
        val fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
        return Auction.newBuilder().also {
            it.id = ObjectId().toHexString()
            it.artworkId = ObjectId().toHexString()
            it.startBid = Random.nextDouble(10.0, 100.0).toBigDecimal().toBigDecimalProto()
            it.startedAt = LocalDateTime.now().toTimestampProto(fixedClock)
            it.finishedAt = LocalDateTime.now().toTimestampProto(fixedClock)
            it.addAllBuyers(listOf(randomBidProto(), randomBidProto(), randomBidProto()))
        }.build()
    }

    fun randomBidProto(): Bid = Bid.newBuilder().also {
        it.bid = Random.nextDouble(10.0, 100.0).toBigDecimal().toBigDecimalProto()
        it.buyerId = ObjectId().toHexString()
    }.build()

    fun randomCreateAuctionRequestProto(artworkId: String = ObjectId().toHexString()): CreateAuctionRequest {
        val fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
        return CreateAuctionRequest.newBuilder().also {
            it.artworkId = artworkId
            it.startBid = Random.nextDouble(10.0, 100.0).toBigDecimal().toBigDecimalProto()
            it.startedAt = LocalDateTime.now().toTimestampProto(fixedClock)
            it.finishedAt = LocalDateTime.now().toTimestampProto(fixedClock)
        }.build()
    }
}
