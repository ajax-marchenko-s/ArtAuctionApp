package ua.marchenko.gateway.auction.service.mapper

import ua.marchenko.commonmodels.auction.Auction as AuctionProto
import ua.marchenko.grpcapi.input.reqreply.auction.FindAuctionByIdResponse as FindAuctionByIdResponseProtoGrpc
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdResponse as FindAuctionByIdResponseProtoInternal
import ua.marchenko.grpcapi.input.reqreply.auction.FindAuctionByIdRequest as FindAuctionByIdRequestProtoGrpc
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdRequest as FindAuctionByIdRequestProtoInternal
import ua.marchenko.grpcapi.input.reqreply.auction.CreateAuctionRequest as CreateAuctionRequestProtoGrpc
import ua.marchenko.grpcapi.input.reqreply.auction.CreateAuctionResponse as CreateAuctionResponseProtoGrpc
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionRequest as CreateAuctionRequestProtoInternal
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionResponse as CreateAuctionResponseProtoInternal
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsResponse as FindAllAuctionsResponseProtoInternal

fun FindAuctionByIdRequestProtoGrpc.toFindAuctionByIdRequestProtoInternal(): FindAuctionByIdRequestProtoInternal {
    return FindAuctionByIdRequestProtoInternal.newBuilder().setId(id).build()
}

fun FindAuctionByIdResponseProtoInternal.toFindAuctionByIdResponseProtoGrpc(): FindAuctionByIdResponseProtoGrpc {
    return when (responseCase!!) {
        FindAuctionByIdResponseProtoInternal.ResponseCase.SUCCESS ->
            success.auction.toFindAuctionByIdSuccessResponseProtoGrpc()

        FindAuctionByIdResponseProtoInternal.ResponseCase.RESPONSE_NOT_SET -> error("Response not set")

        FindAuctionByIdResponseProtoInternal.ResponseCase.FAILURE -> {
            when (failure.errorCase!!) {
                FindAuctionByIdResponseProtoInternal.Failure.ErrorCase.NOT_FOUND_BY_ID ->
                    failure.toFindAuctionByIdFailureResponseProtoGrpc()

                FindAuctionByIdResponseProtoInternal.Failure.ErrorCase.ERROR_NOT_SET ->
                    error(failure.message)
            }
        }
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

        CreateAuctionResponseProtoInternal.ResponseCase.FAILURE -> {
            when (failure.errorCase!!) {
                CreateAuctionResponseProtoInternal.Failure.ErrorCase.INVALID_AUCTION_OPERATION ->
                    failure.toCreateAuctionFailureResponseProtoGrpc()

                CreateAuctionResponseProtoInternal.Failure.ErrorCase.ERROR_NOT_SET ->
                    error(failure.message)
            }
        }
    }
}

fun FindAllAuctionsResponseProtoInternal.toAuctionProtoList(): List<AuctionProto> {
    return when (responseCase!!) {
        FindAllAuctionsResponseProtoInternal.ResponseCase.SUCCESS -> success.auctionsList
        FindAllAuctionsResponseProtoInternal.ResponseCase.FAILURE -> emptyList()
        FindAllAuctionsResponseProtoInternal.ResponseCase.RESPONSE_NOT_SET -> emptyList()
    }
}

private fun AuctionProto.toFindAuctionByIdSuccessResponseProtoGrpc(): FindAuctionByIdResponseProtoGrpc {
    return FindAuctionByIdResponseProtoGrpc.newBuilder().also { builder ->
        builder.successBuilder.auction = this
    }.build()
}

private fun FindAuctionByIdResponseProtoInternal.Failure.toFindAuctionByIdFailureResponseProtoGrpc():
        FindAuctionByIdResponseProtoGrpc {
    return FindAuctionByIdResponseProtoGrpc.newBuilder().also { builder ->
        builder.failureBuilder.message = message.orEmpty()
        if (errorCase == FindAuctionByIdResponseProtoInternal.Failure.ErrorCase.NOT_FOUND_BY_ID) {
            builder.failureBuilder.notFoundByIdBuilder
        }
    }.build()
}

private fun AuctionProto.toCreateAuctionSuccessResponseProtoGrpc():
        CreateAuctionResponseProtoGrpc {
    return CreateAuctionResponseProtoGrpc.newBuilder().also { builder ->
        builder.successBuilder.auction = this
    }.build()
}

private fun CreateAuctionResponseProtoInternal.Failure.toCreateAuctionFailureResponseProtoGrpc():
        CreateAuctionResponseProtoGrpc {
    return CreateAuctionResponseProtoGrpc.newBuilder().also { builder ->
        builder.failureBuilder.message = message.orEmpty()
        if (errorCase == CreateAuctionResponseProtoInternal.Failure.ErrorCase.INVALID_AUCTION_OPERATION) {
            builder.failureBuilder.invalidAuctionOperationBuilder
        }
    }.build()
}
