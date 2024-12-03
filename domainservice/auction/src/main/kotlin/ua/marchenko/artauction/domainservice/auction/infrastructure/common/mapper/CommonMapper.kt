package ua.marchenko.artauction.domainservice.auction.infrastructure.common.mapper

import com.google.protobuf.ByteString
import com.google.protobuf.Timestamp
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import ua.marchenko.artauction.domainservice.auction.domain.Auction
import ua.marchenko.artauction.domainservice.auction.domain.Auction.Bid
import ua.marchenko.commonmodels.auction.Auction.Bid as BidProto
import ua.marchenko.commonmodels.auction.Auction as AuctionProto
import ua.marchenko.commonmodels.general.BigDecimal as BigDecimalProto
import ua.marchenko.commonmodels.general.BigDecimal.BigInteger as BigIntegerProto

object CommonMapper {
    fun Auction.toAuctionProto(clock: Clock): AuctionProto {
        return AuctionProto.newBuilder().also {
            it.id = requireNotNull(id) { "Auction id cannot be null" }
            it.artworkId = artworkId
            it.startBid = startBid.toBigDecimalProto()
            it.startedAt = startedAt.toTimestampProto(clock)
            it.finishedAt = finishedAt.toTimestampProto(clock)
            it.addAllBuyers(buyers.map { bid -> bid.toBidProto() })
        }.build()
    }

    fun AuctionProto.toDomain(clock: Clock): Auction {
        return Auction(
            id = id,
            artworkId = artworkId,
            startBid = startBid.toBigDecimal(),
            startedAt = startedAt.toLocalDateTime(clock),
            finishedAt = finishedAt.toLocalDateTime(clock),
            buyers = buyersList.map { it.toDomain() }
        )
    }

    fun BidProto.toDomain(): Bid {
        return Bid(buyerId, bid.toBigDecimal())
    }

    fun BigDecimal.toBigDecimalProto(): BigDecimalProto {
        return BigDecimalProto.newBuilder().also {
            it.scale = scale()
            it.intVal = unscaledValue().toBigIntegerProto()
        }.build()
    }

    fun BigInteger.toBigIntegerProto(): BigIntegerProto {
        return BigIntegerProto.newBuilder().also {
            it.value = ByteString.copyFrom(toByteArray())
        }.build()
    }

    fun BigDecimalProto.toBigDecimal(): BigDecimal =
        BigDecimal(BigInteger(intVal.value.toByteArray()), scale)

    fun Bid.toBidProto(): BidProto {
        return BidProto.newBuilder().also {
            it.buyerId = buyerId
            it.bid = bid.toBigDecimalProto()
        }.build()
    }

    fun LocalDateTime.toTimestampProto(clock: Clock): Timestamp {
        val instant = atZone(clock.zone).toInstant()
        return Timestamp.newBuilder().also {
            it.seconds = instant.epochSecond
            it.nanos = instant.nano
        }.build()
    }

    fun Timestamp.toLocalDateTime(clock: Clock): LocalDateTime {
        return Instant.ofEpochSecond(seconds, nanos.toLong()).atZone(clock.zone).toLocalDateTime()
    }
}
