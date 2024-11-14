@file:Suppress("TooManyFunctions")

package ua.marchenko.artauction.auction.mapper

import com.google.protobuf.ByteString
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Clock
import java.time.LocalDateTime
import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.core.auction.exception.InvalidAuctionOperationException
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.artauction.common.mongodb.id.toObjectId
import ua.marchenko.core.auction.exception.AuctionNotFoundException
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionResponse as CreateAuctionResponseProto
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionRequest as CreateAuctionRequestProto
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdResponse as FindAuctionByIdResponseProto
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsResponse as FindAllAuctionsResponseProto
import ua.marchenko.commonmodels.auction.Auction.Bid as BidProto
import ua.marchenko.commonmodels.auction.Auction as AuctionProto
import ua.marchenko.commonmodels.general.BigDecimal as BigDecimalProto
import ua.marchenko.commonmodels.general.BigDecimal.BigInteger as BigIntegerProto

fun CreateAuctionRequestProto.toCreateAuctionRequest(clock: Clock): CreateAuctionRequest =
    CreateAuctionRequest(
        artworkId = artworkId,
        startBid = startBid.toBigDecimal(),
        startedAt = startedAt.toLocalDateTime(clock),
        finishedAt = finishedAt.toLocalDateTime(clock)
    )

fun MongoAuction.toFindAuctionByIdSuccessResponseProto(clock: Clock): FindAuctionByIdResponseProto {
    return FindAuctionByIdResponseProto.newBuilder().also { builder ->
        builder.successBuilder.setAuction(toAuctionProto(clock))
    }.build()
}

fun Throwable.toFindAuctionByIdFailureResponseProto(): FindAuctionByIdResponseProto {
    return FindAuctionByIdResponseProto.newBuilder().also { builder ->
        builder.failureBuilder.message = message.orEmpty()
        if (this is AuctionNotFoundException) {
            builder.failureBuilder.notFoundByIdBuilder
        }
    }.build()
}

fun MongoAuction.toCreateAuctionSuccessResponseProto(clock: Clock): CreateAuctionResponseProto {
    return CreateAuctionResponseProto.newBuilder().also { builder ->
        builder.successBuilder.setAuction(toAuctionProto(clock))
    }.build()
}

fun Throwable.toCreateAuctionFailureResponseProto(): CreateAuctionResponseProto {
    return CreateAuctionResponseProto.newBuilder().also { builder ->
        builder.failureBuilder.message = message.orEmpty()
        if (this is InvalidAuctionOperationException) {
            builder.failureBuilder.invalidAuctionOperationBuilder
        }
    }.build()
}

fun List<MongoAuction>.toFindAllAuctionsSuccessResponseProto(clock: Clock): FindAllAuctionsResponseProto {
    return FindAllAuctionsResponseProto.newBuilder().also { builder ->
        builder.successBuilder.addAllAuctions(map { it.toAuctionProto(clock) })
    }.build()
}

fun Throwable.toFindAllAuctionsFailureResponseProto(): FindAllAuctionsResponseProto {
    return FindAllAuctionsResponseProto.newBuilder().also { builder ->
        builder.failureBuilder.message = message.orEmpty()
    }.build()
}

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
