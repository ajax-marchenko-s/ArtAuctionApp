package ua.marchenko.artauction.core.common.exception

open class NotFoundException(override val message: String) : RuntimeException(message)
