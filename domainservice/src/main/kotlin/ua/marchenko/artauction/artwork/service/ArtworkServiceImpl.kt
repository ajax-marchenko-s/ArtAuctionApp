package ua.marchenko.artauction.artwork.service

import java.util.concurrent.TimeUnit
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import ua.marchenko.artauction.artwork.model.MongoArtwork
import ua.marchenko.artauction.artwork.model.projection.ArtworkFull
import ua.marchenko.artauction.artwork.repository.ArtworkRepository
import ua.marchenko.artauction.common.annotation.profiling.annotation.CustomProfiling
import ua.marchenko.artauction.user.service.UserService
import ua.marchenko.core.artwork.enums.ArtworkStatus
import ua.marchenko.core.artwork.exception.ArtworkNotFoundException
import ua.marchenko.core.user.exception.UserNotFoundException

@Service
class ArtworkServiceImpl(
    @Qualifier(value = "redisArtworkRepository")
    private val artworkRepository: ArtworkRepository,
    private val userService: UserService,
) : ArtworkService {

    @CustomProfiling(timeUnit = TimeUnit.MICROSECONDS)
    override fun getAll(page: Int, limit: Int) = artworkRepository.findAll(page, limit)

    override fun getFullAll(page: Int, limit: Int): Flux<ArtworkFull> = artworkRepository.findFullAll(page, limit)

    override fun getById(id: String): Mono<MongoArtwork> =
        artworkRepository.findById(id).switchIfEmpty { Mono.error(ArtworkNotFoundException(id)) }

    override fun getFullById(id: String) =
        artworkRepository.findFullById(id).switchIfEmpty { Mono.error(ArtworkNotFoundException(id)) }

    override fun save(artwork: MongoArtwork): Mono<MongoArtwork> {
        val artistId =
            artwork.artistId?.toHexString() ?: return Mono.error(IllegalArgumentException("Artist ID cannot be null"))
        return userService.existById(artistId)
            .handle { exists, sink ->
                if (exists) sink.next(artwork)
                else sink.error(UserNotFoundException(value = artistId))
            }
            .flatMap { artworkToSave ->
                artworkRepository.save(artworkToSave.copy(status = ArtworkStatus.VIEW))
            }
    }

    override fun update(artworkId: String, artwork: MongoArtwork): Mono<MongoArtwork> =
        artworkRepository.updateById(artworkId, artwork)
            .switchIfEmpty { Mono.error(ArtworkNotFoundException(artworkId)) }

    override fun updateStatusByIdAndPreviousStatus(
        artworkId: String,
        prevStatus: ArtworkStatus,
        newStatus: ArtworkStatus
    ): Mono<MongoArtwork> {
        return artworkRepository.updateStatusByIdAndPreviousStatus(artworkId, prevStatus, newStatus)
    }

    override fun existsById(id: String) = artworkRepository.existsById(id)
}
