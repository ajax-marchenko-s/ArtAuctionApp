package ua.marchenko.artauction.core.artwork.exception

import ua.marchenko.artauction.core.common.exception.NotFoundException

class ArtworkNotFoundException(artworkId: String) : NotFoundException("Artwork with ID $artworkId not found")
