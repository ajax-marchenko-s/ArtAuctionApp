package ua.marchenko.artauction.common.exception.type.general

open class NotFoundException(override val message: String) : RuntimeException(message)
