package ua.marchenko.artauction.gateway.infrastructure.grpc.service

import ua.marchenko.artauction.gateway.infrastructure.auction.AuctionProtoFixture.randomAuctionProto
import ua.marchenko.artauction.gateway.infrastructure.auction.AuctionProtoFixture.randomCreateAuctionRequestProtoGrpc
import ua.marchenko.artauction.gateway.infrastructure.auction.AuctionProtoFixture.randomSuccessCreateAuctionResponseProto
import ua.marchenko.artauction.gateway.infrastructure.auction.AuctionProtoFixture.randomSuccessFindAllAuctionsResponseProto
import ua.marchenko.artauction.gateway.infrastructure.auction.AuctionProtoFixture.randomSuccessFindByIdResponseProto
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
import ua.marchenko.artauction.gateway.application.port.input.AuctionInputPort
import ua.marchenko.artauction.gateway.infrastructure.grpc.mapper.toCreateAuctionRequestProtoInternal
import ua.marchenko.artauction.gateway.infrastructure.grpc.mapper.toCreateAuctionResponseProtoGrpc
import ua.marchenko.artauction.gateway.infrastructure.grpc.mapper.toFindAuctionByIdRequestProtoInternal
import ua.marchenko.artauction.gateway.infrastructure.grpc.mapper.toFindAuctionByIdResponseProtoGrpc
import ua.marchenko.artauction.gateway.getRandomString
import ua.marchenko.internal.input.reqreply.auction.FindAllAuctionsRequest
import ua.marchenko.grpcapi.input.reqreply.auction.FindAuctionByIdRequest as FindAuctionByIdRequestProtoGrpc

class AuctionGrpcServiceTest {

    @MockK
    private lateinit var auctionInputPort: AuctionInputPort

    @InjectMockKs
    private lateinit var auctionGrpcService: AuctionGrpcService

    @Test
    fun `should return CreateAuctionResponseProtoGrpc when create auction success`() {
        // GIVEN
        val request = randomCreateAuctionRequestProtoGrpc()
        val response = randomSuccessCreateAuctionResponseProto()
        every {
            auctionInputPort.createAuction(request.toCreateAuctionRequestProtoInternal())
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
            auctionInputPort.getAuctionById(request.toFindAuctionByIdRequestProtoInternal())
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
            auctionInputPort.getAllAuctions(
                FindAllAuctionsRequest.newBuilder().apply {
                    page = START_PAGE
                    limit = Int.MAX_VALUE
                }.build()
            )
        } returns randomSuccessFindAllAuctionsResponseProto(existedAuctions).toMono()

        every {
            auctionInputPort.subscribeToCreatedAuction()
        } returns auctionsFromNats.toFlux()

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
