package ua.marchenko.artauction.artwork.controller

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
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.marchenko.artauction.artwork.controller.dto.ArtworkFullResponse
import ua.marchenko.artauction.artwork.controller.dto.ArtworkResponse
import ua.marchenko.artauction.artwork.controller.dto.CreateArtworkRequest
import ua.marchenko.artauction.artwork.mapper.toFullResponse
import ua.marchenko.artauction.artwork.mapper.toMongo
import ua.marchenko.artauction.artwork.mapper.toResponse
import ua.marchenko.artauction.artwork.service.ArtworkService

@RestController
@RequestMapping("/api/v1/artworks")
class ArtworkController(private val artworkService: ArtworkService) {

    @GetMapping("{id}")
    fun getArtworkById(@PathVariable id: String): Mono<ArtworkResponse> =
        artworkService.getById(id).map { it.toResponse() }

    @GetMapping("{id}/full")
    fun getFullArtworkById(@PathVariable id: String): Mono<ArtworkFullResponse> =
        artworkService.getFullById(id).map { it.toFullResponse() }

    @GetMapping
    fun getAllArtworks(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): Flux<ArtworkResponse> = artworkService.getAll(page, limit).map { it.toResponse() }

    @GetMapping("/full")
    fun getAllFullArtworks(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): Flux<ArtworkFullResponse> = artworkService.getFullAll(page, limit).map { it.toFullResponse() }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addArtwork(@Valid @RequestBody artwork: CreateArtworkRequest): Mono<ArtworkResponse> =
        artworkService.save(artwork.toMongo()).map { it.toResponse() }
}
