@file:Suppress("TooManyFunctions")

package ua.marchenko.artauction.domainservice.auction.infrastructure.nats.mapper

import java.time.Clock
import ua.marchenko.artauction.domainservice.auction.domain.Auction
import ua.marchenko.artauction.core.auction.exception.AuctionNotFoundException
import ua.marchenko.artauction.core.auction.exception.InvalidAuctionOperationException
import ua.marchenko.artauction.domainservice.auction.domain.CreateAuction
import ua.marchenko.artauction.domainservice.auction.infrastructure.common.mapper.CommonMapper.toAuctionProto
import ua.marchenko.artauction.domainservice.auction.infrastructure.common.mapper.CommonMapper.toBigDecimal
import ua.marchenko.artauction.domainservice.auction.infrastructure.common.mapper.CommonMapper.toLocalDateTime
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionRequest as CreateAuctionRequestProto
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionResponse as CreateAuctionResponseProto
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsResponse as FindAllAuctionsResponseProto
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdResponse as FindAuctionByIdResponseProto

fun CreateAuctionRequestProto.toDomainCreate(clock: Clock): CreateAuction =
    CreateAuction(
        artworkId = artworkId,
        startBid = startBid.toBigDecimal(),
        startedAt = startedAt.toLocalDateTime(clock),
        finishedAt = finishedAt.toLocalDateTime(clock),
    )

fun Auction.toFindAuctionByIdSuccessResponseProto(clock: Clock): FindAuctionByIdResponseProto {
    return FindAuctionByIdResponseProto.newBuilder().apply {
        successBuilder.setAuction(toAuctionProto(clock))
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

fun Auction.toCreateAuctionSuccessResponseProto(clock: Clock): CreateAuctionResponseProto {
    return CreateAuctionResponseProto.newBuilder().apply {
        successBuilder.setAuction(toAuctionProto(clock))
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

fun List<Auction>.toFindAllAuctionsSuccessResponseProto(clock: Clock): FindAllAuctionsResponseProto {
    return FindAllAuctionsResponseProto.newBuilder().apply {
        successBuilder.addAllAuctions(map { it.toAuctionProto(clock) })
    }.build()
}

fun Throwable.toFindAllAuctionsFailureResponseProto(): FindAllAuctionsResponseProto {
    return FindAllAuctionsResponseProto.newBuilder().apply {
        failureBuilder.message = message.orEmpty()
    }.build()
}
