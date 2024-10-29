package ua.marchenko.artauction.user.exception

import ua.marchenko.artauction.common.exception.type.general.AlreadyExistException

class UserAlreadyExistsException : AlreadyExistException("User with the specified attributes already exists")
