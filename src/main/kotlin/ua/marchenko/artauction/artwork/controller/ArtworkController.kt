package ua.marchenko.artauction.artwork.controller

import org.springframework.web.bind.annotation.*
import ua.marchenko.artauction.artwork.controller.dto.ArtworkRequest
import ua.marchenko.artauction.artwork.controller.dto.ArtworkResponse
import ua.marchenko.artauction.artwork.mapper.toArtwork
import ua.marchenko.artauction.artwork.mapper.toArtworkResponse
import ua.marchenko.artauction.artwork.service.ArtworkService

@RestController
@RequestMapping("/api/v1/artwork")
class ArtworkController(private val artworkService: ArtworkService) {

    @GetMapping("{id}")
    fun getArtworkById(@PathVariable id: String): ArtworkResponse {
        return artworkService.findById(id).toArtworkResponse()
    }

    @GetMapping
    fun getAllArtworks(): List<ArtworkResponse> {
        return artworkService.findAll().map { it.toArtworkResponse() }
    }

    @PostMapping
    fun addArtwork(@RequestBody artwork: ArtworkRequest): ArtworkResponse {
        return artworkService.save(artwork.toArtwork()).toArtworkResponse()
    }
}
