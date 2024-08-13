package ua.marchenko.artauction.service.artwork

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ua.marchenko.artauction.dto.artwork.ArtworkRequest
import ua.marchenko.artauction.dto.artwork.ArtworkResponse
import ua.marchenko.artauction.enums.user.Role
import ua.marchenko.artauction.exception.type.artwork.ArtworkNotFoundException
import ua.marchenko.artauction.exception.type.general.IncorrectRoleException
import ua.marchenko.artauction.mapper.artwork.toArtwork
import ua.marchenko.artauction.mapper.artwork.toArtworkResponse
import ua.marchenko.artauction.mapper.user.toUser
import ua.marchenko.artauction.repository.artwork.MongoArtworkRepository
import ua.marchenko.artauction.service.user.UserService

@Service
class ArtworkServiceImpl(
    private val mongoArtworkRepository: MongoArtworkRepository,
    private val userService: UserService
) : ArtworkService {

    override fun findAll(): List<ArtworkResponse> {
        return mongoArtworkRepository.findAll().map { it.toArtworkResponse() }
    }

    override fun findById(id: String): ArtworkResponse {
        return mongoArtworkRepository.findByIdOrNull(id)?.toArtworkResponse() ?: throwArtworkNotFoundException(id)
    }

    override fun save(artworkRequest: ArtworkRequest): ArtworkResponse {
        val artist = userService.findById(artworkRequest.artistId)
        if(artist.role != Role.ARTIST){
            throwIncorrectRoleException("Trying to save artwork without correct artist")
        }
        return mongoArtworkRepository.save(artworkRequest.toArtwork(artist.toUser())).toArtworkResponse()
    }

    private fun throwArtworkNotFoundException(artworkId: String): Nothing {
        throw ArtworkNotFoundException(artworkId)
    }

    private fun throwIncorrectRoleException(message: String): Nothing {
        throw IncorrectRoleException(message)
    }
}