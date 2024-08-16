package ua.marchenko.artauction.common.exception.type.general

open class AlreadyExistException(override val message: String) : RuntimeException(message)