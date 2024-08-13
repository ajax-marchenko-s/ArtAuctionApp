package ua.marchenko.artauction.controller

import org.springframework.web.bind.annotation.*
import ua.marchenko.artauction.dto.artwork.ArtworkRequest
import ua.marchenko.artauction.dto.artwork.ArtworkResponse
import ua.marchenko.artauction.service.artwork.ArtworkService

@RestController
@RequestMapping("/api/v1/artwork")
class ArtworkController(private val artworkService: ArtworkService) {

    @GetMapping("{id}")
    fun getArtworkById(@PathVariable id: String): ArtworkResponse {
        return artworkService.findById(id)
    }

    @GetMapping
    fun getAllArtworks(): List<ArtworkResponse> {
        return artworkService.findAll()
    }

    @PostMapping
    fun addUser( @RequestBody artwork: ArtworkRequest): ArtworkResponse {
        return artworkService.save(artwork)
    }
}