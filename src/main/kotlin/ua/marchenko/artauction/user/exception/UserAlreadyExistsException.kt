package ua.marchenko.artauction.user.exception

import ua.marchenko.artauction.common.exception.type.general.AlreadyExistException

class UserAlreadyExistsException(private val userEmail: String) :
    AlreadyExistException("User with email $userEmail already exists")