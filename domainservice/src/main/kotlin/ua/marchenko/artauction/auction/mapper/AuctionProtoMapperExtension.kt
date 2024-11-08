package ua.marchenko.artauction.auction.mapper

import com.google.protobuf.ByteString
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Clock
import java.time.LocalDateTime
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.artauction.common.mongodb.id.toObjectId
import ua.marchenko.internal.commonmodels.auction.Auction.Bid as BidProto
import ua.marchenko.internal.commonmodels.auction.Auction as AuctionProto
import ua.marchenko.internal.commonmodels.general.BigDecimal as BigDecimalProto
import ua.marchenko.internal.commonmodels.general.BigDecimal.BigInteger as BigIntegerProto

fun MongoAuction.toAuctionProto(clock: Clock): AuctionProto {
    return AuctionProto.newBuilder().also {
        it.id = requireNotNull(id) { "Auction id cannot be null" }.toHexString()
        it.artworkId = requireNotNull(artworkId) { "Artwork id cannot be null" }.toHexString()
        it.startBid = (startBid ?: BigDecimal(0.0)).toBigDecimalProto()
        it.startedAt = (startedAt ?: LocalDateTime.MIN).toTimestampProto(clock)
        it.finishedAt = (finishedAt ?: LocalDateTime.MIN).toTimestampProto(clock)
        it.addAllBuyers(buyers?.map { bid -> bid.toBidProto() }.orEmpty())
    }.build()
}

fun AuctionProto.toMongoAuction(clock: Clock): MongoAuction {
    return MongoAuction(
        id = id.toObjectId(),
        artworkId = artworkId.toObjectId(),
        startBid = startBid.toBigDecimal(),
        startedAt = startedAt.toLocalDateTime(clock),
        finishedAt = finishedAt.toLocalDateTime(clock),
        buyers = buyersList.map { it.toBid() }
    )
}

fun BidProto.toBid(): MongoAuction.Bid {
    return MongoAuction.Bid(buyerId.toObjectId(), bid.toBigDecimal())
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

fun MongoAuction.Bid.toBidProto(): BidProto {
    return BidProto.newBuilder().also {
        it.buyerId = requireNotNull(buyerId) { "Buyer id cannot be null" }.toHexString()
        it.bid = requireNotNull(bid) { "Bid cannot be null" }.toBigDecimalProto()
    }.build()
}
