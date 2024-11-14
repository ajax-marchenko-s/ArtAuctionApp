package auction

import com.google.protobuf.ByteString
import getRandomString
import java.time.Instant
import com.google.protobuf.Timestamp as TimestampProto
import kotlin.random.Random
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
        CreateAuctionRequestProtoGrpc.newBuilder().also {
            it.artworkId = getRandomString()
            it.startBid = getRandomBigDecimal()
            it.startedAt = getRandomTimestamp()
            it.finishedAt = getRandomTimestamp()
        }.build()

    fun randomSuccessCreateAuctionResponseProto(): CreateAuctionResponseProto =
        CreateAuctionResponseProto.newBuilder().also {
            it.successBuilder.auction = randomAuctionProto()
        }.build()

    fun randomSuccessFindByIdResponseProto(): FindAuctionByIdResponseProtoInternal =
        FindAuctionByIdResponseProtoInternal.newBuilder().also {
            it.successBuilder.auction = randomAuctionProto()
        }.build()

    fun randomSuccessFindAllAuctionsResponseProto(auctionList: List<AuctionProto> = List(3) { randomAuctionProto() }): FindAllAuctionsResponseProto =
        FindAllAuctionsResponseProto.newBuilder()
            .also { it.successBuilder.addAllAuctions(auctionList) }.build()

    fun randomAuctionProto(): AuctionProto = AuctionProto.newBuilder().also {
        it.id = getRandomString()
        it.artworkId = getRandomString()
        it.startBid = getRandomBigDecimal()
        it.startedAt = getRandomTimestamp()
        it.finishedAt = getRandomTimestamp()
    }.build()

    fun randomFailureGeneralFindAuctionByIdResponseProtoInternal(): FindAuctionByIdResponseProtoInternal =
        FindAuctionByIdResponseProtoInternal.newBuilder().also { builder ->
            builder.failureBuilder.message = ERROR_MESSAGE
        }.build()

    fun randomFailureAuctionNotFoundFindAuctionByIdResponseProtoInternal(): FindAuctionByIdResponseProtoInternal =
        FindAuctionByIdResponseProtoInternal.newBuilder().also { builder ->
            builder.failureBuilder.notFoundByIdBuilder
            builder.failureBuilder.message = ERROR_MESSAGE
        }.build()

    fun randomFailureGeneralCreateAuctionResponseProtoInternal(): CreateAuctionResponseProtoInternal =
        CreateAuctionResponseProtoInternal.newBuilder().also { builder ->
            builder.failureBuilder.message = ERROR_MESSAGE
        }.build()

    fun randomFailureInvalidAuctionOperationCreateAuctionResponseProtoInternal(): CreateAuctionResponseProtoInternal =
        CreateAuctionResponseProtoInternal.newBuilder().also { builder ->
            builder.failureBuilder.invalidAuctionOperationBuilder
            builder.failureBuilder.message = ERROR_MESSAGE
        }.build()


    private fun getRandomBigDecimal(): BigDecimalProto = BigDecimalProto.newBuilder().also {
        it.scale = Random.nextInt(10, 100)
        it.intVal = getRandomBigInteger()
    }.build()

    private fun getRandomBigInteger(): BigIntegerProto = BigIntegerProto.newBuilder()
        .setValue(ByteString.copyFrom(Random.nextBytes(ByteArray(16))))
        .build()

    private fun getRandomTimestamp(): TimestampProto = TimestampProto.newBuilder().also {
        it.seconds = Random.nextLong(0, Instant.now().epochSecond)
        it.nanos = Random.nextInt(0, 1_000_000_000)
    }.build()

    const val ERROR_MESSAGE = "Error message"
}
