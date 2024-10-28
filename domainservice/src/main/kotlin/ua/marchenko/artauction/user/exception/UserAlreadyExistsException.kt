package ua.marchenko.artauction.user.exception

import ua.marchenko.artauction.common.exception.type.general.AlreadyExistException

class UserAlreadyExistsException(private val field: String = "email", private val value: String) :
    AlreadyExistException("User with $field $value already exists")
