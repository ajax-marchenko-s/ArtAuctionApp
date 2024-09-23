package ua.marchenko.artauction.artwork.service

import java.util.concurrent.TimeUnit
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.artwork.exception.ArtworkNotFoundException
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.repository.ArtworkRepository
import ua.marchenko.artauction.common.annotation.profiling.annotation.CustomProfiling
import ua.marchenko.artauction.common.mongodb.id.toObjectId
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.artauction.user.service.UserService

@Service
class ArtworkServiceImpl(
    private val artworkRepository: ArtworkRepository,
    private val userService: UserService,
) : ArtworkService {

    @CustomProfiling(timeUnit = TimeUnit.MICROSECONDS)
    override fun getAll() = artworkRepository.findAll()

    override fun getFullAll() = artworkRepository.findFullAll()

    override fun getById(id: String) = artworkRepository.findById(id) ?: throw ArtworkNotFoundException(id)

    override fun getFullById(id: String) = artworkRepository.findFullById(id) ?: throw ArtworkNotFoundException(id)

    override fun save(artwork: MongoArtwork): MongoArtwork {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        val artist: MongoUser = userService.getByEmail(authentication.name)
        val artworkToSave = artwork.copy(artistId = artist.id, status = ArtworkStatus.VIEW)
        return artworkRepository.save(artworkToSave)
    }

    override fun update(artworkId: String, artwork: MongoArtwork): MongoArtwork {
        val artworkFromDB = getById(artworkId)
        val updatedArtwork = artwork.copy(
            id = artworkId.toObjectId(),
            status = artworkFromDB.status,
            artistId = artworkFromDB.artistId
        )
        return artworkRepository.save(updatedArtwork)
    }

    override fun updateStatus(artworkId: String, status: ArtworkStatus): MongoArtwork {
        val artworkFromDB = getById(artworkId)
        return artworkRepository.save(artworkFromDB.copy(status = status))
    }

    override fun existsById(id: String) = artworkRepository.existsById(id)
}
