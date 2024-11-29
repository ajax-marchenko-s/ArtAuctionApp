package ua.marchenko.artauction.gateway.infrastructure.rest

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
import ua.marchenko.artauction.gateway.application.port.input.ArtworkMessageHandlerInputPort
import ua.marchenko.artauction.gateway.infrastructure.rest.dto.ArtworkFullResponse
import ua.marchenko.artauction.gateway.infrastructure.rest.dto.ArtworkResponse
import ua.marchenko.artauction.gateway.infrastructure.rest.dto.CreateArtworkRequest
import ua.marchenko.artauction.gateway.infrastructure.rest.mapper.toArtworkFullResponse
import ua.marchenko.artauction.gateway.infrastructure.rest.mapper.toArtworkResponse
import ua.marchenko.artauction.gateway.infrastructure.rest.mapper.toArtworksList
import ua.marchenko.artauction.gateway.infrastructure.rest.mapper.toCreateArtworkRequestProto
import ua.marchenko.artauction.gateway.infrastructure.rest.mapper.toFullArtworkList
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksFullRequest
import ua.marchenko.internal.input.reqreply.artwork.FindAllArtworksRequest
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkByIdRequest
import ua.marchenko.internal.input.reqreply.artwork.FindArtworkFullByIdRequest

@RestController
@RequestMapping("/api/v1/artworks")
class ArtworkController(
    private val artworkMessageHandlerInputPort: ArtworkMessageHandlerInputPort,
) {
    @GetMapping("{id}")
    fun getArtworkById(@PathVariable id: String): Mono<ArtworkResponse> {
        val request = FindArtworkByIdRequest.newBuilder().setId(id).build()
        return artworkMessageHandlerInputPort.getArtworkById(request).map { it.toArtworkResponse() }
    }

    @GetMapping("{id}/full")
    fun getFullArtworkById(@PathVariable id: String): Mono<ArtworkFullResponse> {
        val request = FindArtworkFullByIdRequest.newBuilder().setId(id).build()
        return artworkMessageHandlerInputPort.getFullArtworkById(request).map { it.toArtworkFullResponse() }
    }

    @GetMapping
    fun getAllArtworks(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): Mono<List<ArtworkResponse>> {
        val request = FindAllArtworksRequest.newBuilder().setPage(page).setLimit(limit).build()
        return artworkMessageHandlerInputPort.getAllArtworks(request).map { it.toArtworksList() }
    }

    @GetMapping("/full")
    fun getAllFullArtworks(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): Mono<List<ArtworkFullResponse>> {
        val request = FindAllArtworksFullRequest.newBuilder().setPage(page).setLimit(limit).build()
        return artworkMessageHandlerInputPort.getAllFullArtworks(request).map { it.toFullArtworkList() }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addArtwork(@Valid @RequestBody artwork: CreateArtworkRequest): Mono<ArtworkResponse> {
        return artworkMessageHandlerInputPort.createArtwork(artwork.toCreateArtworkRequestProto())
            .map { it.toArtworkResponse() }
    }
}
