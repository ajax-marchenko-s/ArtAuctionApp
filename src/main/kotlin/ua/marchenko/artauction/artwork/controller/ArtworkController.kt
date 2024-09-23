package ua.marchenko.artauction.artwork.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import ua.marchenko.artauction.artwork.controller.dto.CreateArtworkRequest
import ua.marchenko.artauction.artwork.mapper.toArtwork
import ua.marchenko.artauction.artwork.mapper.toArtworkFullResponse
import ua.marchenko.artauction.artwork.mapper.toArtworkResponse
import ua.marchenko.artauction.artwork.service.ArtworkService

@RestController
@RequestMapping("/api/v1/artworks")
class ArtworkController(private val artworkService: ArtworkService) {

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getArtworkById(@PathVariable id: String) = artworkService.getById(id).toArtworkResponse()

    @GetMapping("{id}/full")
    @ResponseStatus(HttpStatus.OK)
    fun getFullArtworkById(@PathVariable id: String) = artworkService.getFullById(id).toArtworkFullResponse()

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getAllArtworks() = artworkService.getAll().map { it.toArtworkResponse() }

    @GetMapping("/full")
    @ResponseStatus(HttpStatus.OK)
    fun getAllFullArtworks() = artworkService.getFullAll().map { it.toArtworkFullResponse() }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addArtwork(@Valid @RequestBody artwork: CreateArtworkRequest) =
        artworkService.save(artwork.toArtwork()).toArtworkResponse()
}
