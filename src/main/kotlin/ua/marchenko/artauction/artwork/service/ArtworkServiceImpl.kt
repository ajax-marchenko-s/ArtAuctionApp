package ua.marchenko.artauction.artwork.service

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.artwork.exception.ArtworkNotFoundException
import ua.marchenko.artauction.artwork.model.Artwork
import ua.marchenko.artauction.artwork.repository.ArtworkRepository
import ua.marchenko.artauction.user.model.User
import ua.marchenko.artauction.user.service.UserService

@Service
class ArtworkServiceImpl(
    private val artworkRepository: ArtworkRepository,
    private val userService: UserService
) : ArtworkService {

    override fun findAll(): List<Artwork> {
        return artworkRepository.getAll()
    }

    override fun findById(id: String): Artwork {
        return artworkRepository.getByIdOrNull(id) ?: throwArtworkNotFoundException(id)
    }

    override fun save(artwork: Artwork): Artwork {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        val artist: User = userService.findByEmail(authentication.name)
        val artworkToSave = artwork.copy(artist = artist, status = ArtworkStatus.VIEW)
        return artworkRepository.save(artworkToSave)
    }

    override fun update(artworkId: String, artwork: Artwork, isStatusUpdated: Boolean): Artwork {
        val savedArtwork = findById(artworkId)
        val updatedArtwork = artwork.copy(
            id = artworkId,
            status = if (isStatusUpdated) artwork.status else savedArtwork.status,
            artist = savedArtwork.artist
        )
        return artworkRepository.save(updatedArtwork)
    }

    override fun existsById(id: String): Boolean {
        return artworkRepository.existsById(id)
    }

    private fun throwArtworkNotFoundException(artworkId: String): Nothing {
        throw ArtworkNotFoundException(artworkId)
    }
}
