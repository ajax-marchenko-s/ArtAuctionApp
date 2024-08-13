package ua.marchenko.artauction.exception.type.general

open class NotFoundException (override val message: String) : RuntimeException(message)