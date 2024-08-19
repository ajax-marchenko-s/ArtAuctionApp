package ua.marchenko.artauction.artwork.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ua.marchenko.artauction.artwork.controller.dto.ArtworkRequest
import ua.marchenko.artauction.artwork.mapper.toArtwork
import ua.marchenko.artauction.artwork.mapper.toArtworkResponse
import ua.marchenko.artauction.artwork.service.ArtworkService

@RestController
@RequestMapping("/api/v1/artwork")
class ArtworkController(private val artworkService: ArtworkService) {

    @GetMapping("{id}")
    fun getArtworkById(@PathVariable id: String) = artworkService.findById(id).toArtworkResponse()

    @GetMapping
    fun getAllArtworks() = artworkService.findAll().map { it.toArtworkResponse() }

    @PostMapping
    fun addArtwork(@RequestBody artwork: ArtworkRequest) = artworkService.save(artwork.toArtwork()).toArtworkResponse()

}
