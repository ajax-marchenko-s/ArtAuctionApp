package ua.marchenko.artauction.artwork.service

import java.util.concurrent.TimeUnit
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.artwork.exception.ArtworkNotFoundException
import ua.marchenko.artauction.artwork.model.Artwork
import ua.marchenko.artauction.artwork.repository.ArtworkRepository
import ua.marchenko.artauction.common.annotation.profiling.annotation.CustomProfiling
import ua.marchenko.artauction.common.mongodb.id.toObjectId
import ua.marchenko.artauction.user.model.User
import ua.marchenko.artauction.user.service.UserService

@Service
class ArtworkServiceImpl(
    private val artworkRepository: ArtworkRepository,
    private val userService: UserService,
) : ArtworkService {

    @CustomProfiling(timeUnit = TimeUnit.MICROSECONDS)
    override fun getAll() = artworkRepository.findAll()

    override fun getById(id: String) = artworkRepository.findById(id) ?: throw ArtworkNotFoundException(id)

    override fun save(artwork: Artwork): Artwork {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        val artist: User = userService.getByEmail(authentication.name)
        val artworkToSave = artwork.copy(artist = artist, status = ArtworkStatus.VIEW)
        return artworkRepository.save(artworkToSave)
    }

    override fun update(artworkId: String, artwork: Artwork): Artwork {
        val artworkFromDB = getById(artworkId)
        val updatedArtwork = artwork.copy(
            id = artworkId.toObjectId(),
            status = artworkFromDB.status,
            artist = artworkFromDB.artist
        )
        return artworkRepository.save(updatedArtwork)
    }

    override fun updateStatus(artworkId: String, status: ArtworkStatus): Artwork {
        val artworkFromDB = getById(artworkId)
        return artworkRepository.save(artworkFromDB.copy(status = status))
    }

    override fun existsById(id: String) = artworkRepository.existsById(id)

}
