package ua.marchenko.artauction.artwork.service

import java.util.concurrent.TimeUnit
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import ua.marchenko.artauction.artwork.enums.ArtworkStatus
import ua.marchenko.artauction.artwork.exception.ArtworkNotFoundException
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.artwork.repository.ArtworkRepository
import ua.marchenko.artauction.common.annotation.profiling.annotation.CustomProfiling
import ua.marchenko.artauction.common.mongodb.id.toObjectId
import ua.marchenko.artauction.user.service.UserService

@Service
class ArtworkServiceImpl(
    private val artworkRepository: ArtworkRepository,
    private val userService: UserService,
) : ArtworkService {

    @CustomProfiling(timeUnit = TimeUnit.MICROSECONDS)
    override fun getAll(page: Int, limit: Int) = artworkRepository.findAll(page, limit)

    override fun getFullAll(page: Int, limit: Int): Flux<ArtworkFull> = artworkRepository.findFullAll(page, limit)

    override fun getById(id: String): Mono<MongoArtwork> =
        artworkRepository.findById(id).switchIfEmpty(Mono.error(ArtworkNotFoundException(id)))

    override fun getFullById(id: String) =
        artworkRepository.findFullById(id).switchIfEmpty { Mono.error(ArtworkNotFoundException(id)) }

    override fun save(artwork: MongoArtwork): Mono<MongoArtwork> {
        return ReactiveSecurityContextHolder.getContext()
            .map { it.authentication }
            .flatMap { authentication ->
                userService.getByEmail(authentication.name)
                    .flatMap { artist ->
                        val artworkToSave = artwork.copy(artistId = artist.id, status = ArtworkStatus.VIEW)
                        artworkRepository.save(artworkToSave)
                    }
            }
    }


    override fun update(artworkId: String, artwork: MongoArtwork): Mono<MongoArtwork> {
        return getById(artworkId)
            .flatMap { artworkFromDB ->
                val updatedArtwork = artwork.copy(
                    id = artworkId.toObjectId(),
                    status = artworkFromDB.status,
                    artistId = artworkFromDB.artistId
                )
                artworkRepository.save(updatedArtwork)
            }
    }

    override fun updateStatus(artworkId: String, status: ArtworkStatus): Mono<MongoArtwork> {
        return getById(artworkId)
            .flatMap { artworkFromDB ->
                artworkRepository.save(artworkFromDB.copy(status = status))
            }
    }

    override fun existsById(id: String) = artworkRepository.existsById(id)
}
