package ua.marchenko.gateway.artwork.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ua.marchenko.gateway.artwork.mapper.toArtworkFullResponse
import ua.marchenko.gateway.artwork.mapper.toArtworkResponse
import ua.marchenko.gateway.artwork.mapper.toArtworksList
import ua.marchenko.gateway.artwork.mapper.toCreateArtworkRequestProto
import ua.marchenko.gateway.artwork.mapper.toFullArtworkList
import ua.marchenko.gateway.common.nats.NatsClient
import ua.marchenko.internal.NatsSubject
import ua.marchenko.core.artwork.dto.ArtworkFullResponse
import ua.marchenko.core.artwork.dto.ArtworkResponse
import ua.marchenko.core.artwork.dto.CreateArtworkRequest
import ua.marchenko.internal.input.reqreply.artwork.CreateArtworkResponse as CreateArtworkResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksRequest as FindAllArtworksRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullRequest as FindAllArtworksFullRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullResponse as FindAllArtworksFullResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdRequest as FindArtworkByIdRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdResponse as FindArtworkByIdResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdResponse as FindArtworkFullByIdResponseProto
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdRequest as FindArtworkFullByIdRequestProto
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksResponse as FindAllArtworksResponseProto

@RestController
@RequestMapping("/api/v1/artworks")
class ArtworkController(private val natsClient: NatsClient) {

    @GetMapping("{id}")
    fun getArtworkById(@PathVariable id: String): Mono<ArtworkResponse> {
        val request = FindArtworkByIdRequestProto.newBuilder().setId(id).build()
        return natsClient.doRequest(
            subject = NatsSubject.ArtworkNatsSubject.FIND_BY_ID,
            payload = request,
            parser = FindArtworkByIdResponseProto.parser()
        ).map { it.toArtworkResponse() }
    }

    @GetMapping("{id}/full")
    fun getFullArtworkById(@PathVariable id: String): Mono<ArtworkFullResponse> {
        val request = FindArtworkFullByIdRequestProto.newBuilder().setId(id).build()
        return natsClient.doRequest(
            subject = NatsSubject.ArtworkNatsSubject.FIND_BY_ID_FULL,
            payload = request,
            parser = FindArtworkFullByIdResponseProto.parser()
        ).map { it.toArtworkFullResponse() }
    }

    @GetMapping
    fun getAllArtworks(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): Mono<List<ArtworkResponse>> {
        val request = FindAllArtworksRequestProto.newBuilder().setPage(page).setLimit(limit).build()
        return natsClient.doRequest(
            subject = NatsSubject.ArtworkNatsSubject.FIND_ALL,
            payload = request,
            parser = FindAllArtworksResponseProto.parser()
        ).map { it.toArtworksList() }
    }

    @GetMapping("/full")
    fun getAllFullArtworks(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): Mono<List<ArtworkFullResponse>> {
        val request = FindAllArtworksFullRequestProto.newBuilder().setPage(page).setLimit(limit).build()
        return natsClient.doRequest(
            subject = NatsSubject.ArtworkNatsSubject.FIND_ALL_FULL,
            payload = request,
            parser = FindAllArtworksFullResponseProto.parser()
        ).map { it.toFullArtworkList() }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addArtwork(@Valid @RequestBody artwork: CreateArtworkRequest): Mono<ArtworkResponse> {
        return natsClient.doRequest(
            subject = NatsSubject.ArtworkNatsSubject.CREATE,
            payload = artwork.toCreateArtworkRequestProto(),
            parser = CreateArtworkResponseProto.parser()
        ).map { it.toArtworkResponse() }
    }
}
