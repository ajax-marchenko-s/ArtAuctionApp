package ua.marchenko.artauction.domainservice.common.application.exception.type

open class AlreadyExistException(override val message: String) : RuntimeException(message)
