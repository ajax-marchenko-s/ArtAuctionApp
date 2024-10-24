package ua.marchenko.core.artwork.exception

import ua.marchenko.core.common.exception.NotFoundException

class ArtworkNotFoundException(artworkId: String) : NotFoundException("Artwork with ID $artworkId not found")
