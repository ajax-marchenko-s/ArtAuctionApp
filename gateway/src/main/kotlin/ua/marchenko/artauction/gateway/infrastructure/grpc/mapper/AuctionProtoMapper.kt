package ua.marchenko.artauction.gateway.infrastructure.grpc.mapper

import ua.marchenko.commonmodels.auction.Auction as AuctionProto
import ua.marchenko.artauction.core.auction.exception.AuctionNotFoundException
import ua.marchenko.artauction.core.auction.exception.InvalidAuctionOperationException
import ua.marchenko.grpcapi.input.reqreply.auction.CreateAuctionResponse as CreateAuctionResponseProtoGrpc
import ua.marchenko.grpcapi.input.reqreply.auction.CreateAuctionRequest as CreateAuctionRequestProtoGrpc
import ua.marchenko.grpcapi.input.reqreply.auction.FindAuctionByIdResponse as FindAuctionByIdResponseProtoGrpc
import ua.marchenko.grpcapi.input.reqreply.auction.FindAuctionByIdRequest as FindAuctionByIdRequestProtoGrpc
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionResponse as CreateAuctionResponseProtoInternal
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsResponse as FindAllAuctionsResponseProtoInternal
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdRequest as FindAuctionByIdRequestProtoInternal
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdResponse as FindAuctionByIdResponseProtoInternal
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionRequest as CreateAuctionRequestProtoInternal

fun FindAuctionByIdRequestProtoGrpc.toFindAuctionByIdRequestProtoInternal(): FindAuctionByIdRequestProtoInternal =
    FindAuctionByIdRequestProtoInternal.newBuilder().setId(id).build()

fun FindAuctionByIdResponseProtoInternal.toFindAuctionByIdResponseProtoGrpc(): FindAuctionByIdResponseProtoGrpc {
    return when (responseCase!!) {
        FindAuctionByIdResponseProtoInternal.ResponseCase.SUCCESS ->
            success.auction.toFindAuctionByIdSuccessResponseProtoGrpc()

        FindAuctionByIdResponseProtoInternal.ResponseCase.RESPONSE_NOT_SET -> error("Response not set")
        FindAuctionByIdResponseProtoInternal.ResponseCase.FAILURE -> failure.toException()
    }
}

fun CreateAuctionRequestProtoGrpc.toCreateAuctionRequestProtoInternal(): CreateAuctionRequestProtoInternal {
    return CreateAuctionRequestProtoInternal.newBuilder().also {
        it.artworkId = artworkId
        it.startBid = startBid
        it.startedAt = startedAt
        it.finishedAt = finishedAt
    }.build()
}

fun CreateAuctionResponseProtoInternal.toCreateAuctionResponseProtoGrpc(): CreateAuctionResponseProtoGrpc {
    return when (responseCase!!) {
        CreateAuctionResponseProtoInternal.ResponseCase.SUCCESS ->
            success.auction.toCreateAuctionSuccessResponseProtoGrpc()

        CreateAuctionResponseProtoInternal.ResponseCase.RESPONSE_NOT_SET -> error("Response not set")
        CreateAuctionResponseProtoInternal.ResponseCase.FAILURE -> failure.toException()
    }
}

fun FindAllAuctionsResponseProtoInternal.toAuctionProtoList(): List<AuctionProto> {
    return when (responseCase!!) {
        FindAllAuctionsResponseProtoInternal.ResponseCase.SUCCESS -> success.auctionsList
        FindAllAuctionsResponseProtoInternal.ResponseCase.FAILURE -> error(failure.message)
        FindAllAuctionsResponseProtoInternal.ResponseCase.RESPONSE_NOT_SET -> error("Response not set")
    }
}

private fun CreateAuctionResponseProtoInternal.Failure.toException(): Nothing {
    throw when (errorCase!!) {
        CreateAuctionResponseProtoInternal.Failure.ErrorCase.INVALID_AUCTION_OPERATION ->
            InvalidAuctionOperationException(message)

        CreateAuctionResponseProtoInternal.Failure.ErrorCase.ERROR_NOT_SET ->
            error(message)
    }
}

private fun FindAuctionByIdResponseProtoInternal.Failure.toException(): Nothing {
    throw when (errorCase!!) {
        FindAuctionByIdResponseProtoInternal.Failure.ErrorCase.NOT_FOUND_BY_ID ->
            AuctionNotFoundException(message = message)

        FindAuctionByIdResponseProtoInternal.Failure.ErrorCase.ERROR_NOT_SET ->
            error(message)
    }
}

private fun AuctionProto.toFindAuctionByIdSuccessResponseProtoGrpc(): FindAuctionByIdResponseProtoGrpc =
    FindAuctionByIdResponseProtoGrpc.newBuilder().also { builder ->
        builder.successBuilder.auction = this
    }.build()

private fun AuctionProto.toCreateAuctionSuccessResponseProtoGrpc(): CreateAuctionResponseProtoGrpc =
    CreateAuctionResponseProtoGrpc.newBuilder().also { builder ->
        builder.successBuilder.auction = this
    }.build()
