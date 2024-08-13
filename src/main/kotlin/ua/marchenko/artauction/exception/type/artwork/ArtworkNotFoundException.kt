package ua.marchenko.artauction.exception.type.artwork

import ua.marchenko.artauction.exception.type.general.NotFoundException

class ArtworkNotFoundException(artworkId: String) : NotFoundException("Artwork with ID $artworkId not found")
