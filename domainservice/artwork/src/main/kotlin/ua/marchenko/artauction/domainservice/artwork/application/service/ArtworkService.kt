package ua.marchenko.artauction.domainservice.artwork.application.service

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import ua.marchenko.artauction.domainservice.user.application.port.input.UserServiceInputPort
import ua.marchenko.artauction.core.artwork.exception.ArtworkNotFoundException
import ua.marchenko.artauction.core.user.exception.UserNotFoundException
import ua.marchenko.artauction.domainservice.artwork.application.port.input.ArtworkServiceInputPort
import ua.marchenko.artauction.domainservice.artwork.application.port.output.ArtworkRepositoryOutputPort
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork
import ua.marchenko.artauction.domainservice.artwork.domain.Artwork.ArtworkStatus
import ua.marchenko.artauction.domainservice.artwork.domain.projection.ArtworkFull
import ua.marchenko.artauction.domainservice.artwork.domain.CreateArtwork

@Service
class ArtworkService(
    @Qualifier("redisArtworkRepository")
    private val artworkRepository: ArtworkRepositoryOutputPort,
    private val userService: UserServiceInputPort,
) : ArtworkServiceInputPort {

    override fun getAll(page: Int, limit: Int) = artworkRepository.findAll(page, limit)

    override fun getFullAll(page: Int, limit: Int): Flux<ArtworkFull> = artworkRepository.findFullAll(page, limit)

    override fun getById(id: String): Mono<Artwork> =
        artworkRepository.findById(id).switchIfEmpty { Mono.error(ArtworkNotFoundException(id)) }

    override fun getFullById(id: String) =
        artworkRepository.findFullById(id).switchIfEmpty { Mono.error(ArtworkNotFoundException(id)) }

    override fun save(artwork: CreateArtwork): Mono<Artwork> {
        return userService.existById(artwork.artistId)
            .handle { exists, sink ->
                if (exists) sink.next(artwork)
                else sink.error(UserNotFoundException(value = artwork.artistId))
            }
            .flatMap { artworkToSave ->
                artworkRepository.save(artworkToSave)
            }
    }

    override fun update(artworkId: String, artwork: Artwork): Mono<Artwork> {
        val nonUpdatableFields = listOf(
            Artwork::id.name,
            Artwork::status.name,
            Artwork::artistId.name
        )
        return artworkRepository.updateById(artworkId, artwork, nonUpdatableFields)
            .switchIfEmpty { Mono.error(ArtworkNotFoundException(artworkId)) }
    }

    override fun updateStatusByIdAndPreviousStatus(
        artworkId: String,
        prevStatus: ArtworkStatus,
        newStatus: ArtworkStatus
    ): Mono<Artwork> {
        return artworkRepository.updateStatusByIdAndPreviousStatus(artworkId, prevStatus, newStatus)
    }

    override fun existsById(id: String) = artworkRepository.existsById(id)
}
