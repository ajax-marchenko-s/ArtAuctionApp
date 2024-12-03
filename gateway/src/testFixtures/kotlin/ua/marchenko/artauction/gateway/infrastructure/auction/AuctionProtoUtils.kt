package ua.marchenko.artauction.gateway.infrastructure.auction

import com.google.protobuf.ByteString
import java.time.Instant
import com.google.protobuf.Timestamp as TimestampProto
import kotlin.random.Random
import ua.marchenko.artauction.gateway.getRandomString
import ua.marchenko.commonmodels.auction.Auction as AuctionProto
import ua.marchenko.grpcapi.input.reqreply.auction.CreateAuctionRequest as CreateAuctionRequestProtoGrpc
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionResponse as CreateAuctionResponseProto
import ua.marchenko.commonmodels.general.BigDecimal as BigDecimalProto
import ua.marchenko.commonmodels.general.BigDecimal.BigInteger as BigIntegerProto
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdResponse as FindAuctionByIdResponseProtoInternal
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsResponse as FindAllAuctionsResponseProto
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionResponse as CreateAuctionResponseProtoInternal

object AuctionProtoFixture {

    fun randomCreateAuctionRequestProtoGrpc(): CreateAuctionRequestProtoGrpc =
        CreateAuctionRequestProtoGrpc.newBuilder().apply {
            artworkId = getRandomString()
            startBid = randomBigDecimal()
            startedAt = randomTimestamp()
            finishedAt = randomTimestamp()
        }.build()

    fun randomSuccessCreateAuctionResponseProto(): CreateAuctionResponseProto =
        CreateAuctionResponseProto.newBuilder().apply {
            successBuilder.auction = randomAuctionProto()
        }.build()

    fun randomSuccessFindByIdResponseProto(): FindAuctionByIdResponseProtoInternal =
        FindAuctionByIdResponseProtoInternal.newBuilder().apply {
            successBuilder.auction = randomAuctionProto()
        }.build()

    fun randomSuccessFindAllAuctionsResponseProto(auctionList: List<AuctionProto> = List(3) { randomAuctionProto() }): FindAllAuctionsResponseProto =
        FindAllAuctionsResponseProto.newBuilder()
            .apply { successBuilder.addAllAuctions(auctionList) }.build()

    fun randomAuctionProto(): AuctionProto = AuctionProto.newBuilder().apply {
        id = getRandomString()
        artworkId = getRandomString()
        startBid = randomBigDecimal()
        startedAt = randomTimestamp()
        finishedAt = randomTimestamp()
    }.build()

    fun randomFailureGeneralFindAuctionByIdResponseProtoInternal(): FindAuctionByIdResponseProtoInternal =
        FindAuctionByIdResponseProtoInternal.newBuilder().apply {
            failureBuilder.message = ERROR_MESSAGE
        }.build()

    fun randomFailureAuctionNotFoundFindAuctionByIdResponseProtoInternal(): FindAuctionByIdResponseProtoInternal =
        FindAuctionByIdResponseProtoInternal.newBuilder().apply {
            failureBuilder.notFoundByIdBuilder
            failureBuilder.message = ERROR_MESSAGE
        }.build()

    fun randomFailureGeneralCreateAuctionResponseProtoInternal(): CreateAuctionResponseProtoInternal =
        CreateAuctionResponseProtoInternal.newBuilder().apply {
            failureBuilder.message = ERROR_MESSAGE
        }.build()

    fun randomFailureInvalidAuctionOperationCreateAuctionResponseProtoInternal(): CreateAuctionResponseProtoInternal =
        CreateAuctionResponseProtoInternal.newBuilder().apply {
            failureBuilder.invalidAuctionOperationBuilder
            failureBuilder.message = ERROR_MESSAGE
        }.build()


    private fun randomBigDecimal(): BigDecimalProto = BigDecimalProto.newBuilder().apply {
        scale = Random.nextInt(10, 100)
        intVal = randomBigInteger()
    }.build()

    private fun randomBigInteger(): BigIntegerProto = BigIntegerProto.newBuilder().apply {
        value = ByteString.copyFrom(Random.nextBytes(ByteArray(16)))
    }.build()

    private fun randomTimestamp(): TimestampProto = TimestampProto.newBuilder().apply {
        seconds = Random.nextLong(0, Instant.now().epochSecond)
        nanos = Random.nextInt(0, 1_000_000_000)
    }.build()

    const val ERROR_MESSAGE = "Error message"
}
