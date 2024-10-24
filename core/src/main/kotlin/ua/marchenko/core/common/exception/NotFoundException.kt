package ua.marchenko.core.common.exception

open class NotFoundException(override val message: String) : RuntimeException(message)
