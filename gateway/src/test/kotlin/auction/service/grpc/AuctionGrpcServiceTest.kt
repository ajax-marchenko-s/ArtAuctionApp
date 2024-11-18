package auction.service.grpc

import auction.AuctionProtoFixture.randomAuctionProto
import auction.AuctionProtoFixture.randomCreateAuctionRequestProtoGrpc
import auction.AuctionProtoFixture.randomSuccessCreateAuctionResponseProto
import auction.AuctionProtoFixture.randomSuccessFindAllAuctionsResponseProto
import auction.AuctionProtoFixture.randomSuccessFindByIdResponseProto
import getRandomString
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import ua.marchenko.gateway.auction.service.AuctionGrpcService
import ua.marchenko.gateway.auction.mapper.toCreateAuctionRequestProtoInternal
import ua.marchenko.gateway.auction.mapper.toCreateAuctionResponseProtoGrpc
import ua.marchenko.gateway.auction.mapper.toFindAuctionByIdRequestProtoInternal
import ua.marchenko.gateway.auction.mapper.toFindAuctionByIdResponseProtoGrpc
import ua.marchenko.gateway.common.nats.NatsClient
import ua.marchenko.internal.NatsSubject
import ua.marchenko.internal.input.reqreply.auction.CreateAuctionResponse
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsRequest
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsResponse
import ua.marchenko.internal.input.reqreply.auction.FindAuctionByIdResponse
import ua.marchenko.grpcapi.input.reqreply.auction.FindAuctionByIdRequest as FindAuctionByIdRequestProtoGrpc

class AuctionGrpcServiceTest {

    @MockK
    private lateinit var natsClient: NatsClient

    @InjectMockKs
    private lateinit var auctionGrpcService: AuctionGrpcService

    @Test
    fun `should return CreateAuctionResponseProtoGrpc when create auction success`() {
        // GIVEN
        val request = randomCreateAuctionRequestProtoGrpc()
        val response = randomSuccessCreateAuctionResponseProto()
        every {
            natsClient.doRequest(
                subject = NatsSubject.Auction.CREATE,
                payload = request.toCreateAuctionRequestProtoInternal(),
                parser = CreateAuctionResponse.parser()
            )
        } returns response.toMono()

        // WHEN
        val result = auctionGrpcService.createAuction(request)

        // THEN
        result.test()
            .expectNext(response.toCreateAuctionResponseProtoGrpc())
            .verifyComplete()
    }

    @Test
    fun `should return FindAuctionByIdResponseProtoGrpc when artwork with this id exists`() {
        // GIVEN
        val id = getRandomString()
        val request = FindAuctionByIdRequestProtoGrpc.newBuilder().also { it.id = id }.build()
        val response = randomSuccessFindByIdResponseProto()
        every {
            natsClient.doRequest(
                subject = NatsSubject.Auction.FIND_BY_ID,
                payload = request.toFindAuctionByIdRequestProtoInternal(),
                parser = FindAuctionByIdResponse.parser()
            )
        } returns response.toMono()

        // WHEN
        val result = auctionGrpcService.findAuctionById(request)

        // THEN
        result.test()
            .expectNext(response.toFindAuctionByIdResponseProtoGrpc())
            .verifyComplete()
    }

    @Test
    fun `should return stream of all Auctions compared with new auction from Nats`() {
        // GIVEN
        val existedAuctions = List(3) { randomAuctionProto() }
        val auctionsFromNats = List(3) { randomAuctionProto() }

        every {
            natsClient.doRequest(
                subject = NatsSubject.Auction.FIND_ALL,
                payload = FindAllAuctionsRequest.newBuilder().apply {
                    page = START_PAGE
                    limit = Int.MAX_VALUE
                }.build(),
                parser = FindAllAuctionsResponse.parser()
            )
        } returns randomSuccessFindAllAuctionsResponseProto(existedAuctions).toMono()

        every { natsClient.subscribeToCreatedAuction() } returns auctionsFromNats.toFlux()

        // WHEN
        val result = auctionGrpcService.subscribeToAllAuctions(Mono.empty())

        // THEN
        result.collectList()
            .test()
            .assertNext {
                assertEquals((auctionsFromNats + existedAuctions).size, it.size)
                assertTrue(
                    it.containsAll(auctionsFromNats + existedAuctions),
                    "Result should contains ${auctionsFromNats + existedAuctions}, but contains only $it"
                )
            }
            .verifyComplete()
    }

    companion object {
        private const val START_PAGE = 0
    }
}
