package ua.marchenko.artauction.domainservice.user.application.exception

import ua.marchenko.artauction.domainservice.common.application.exception.type.AlreadyExistException

class UserAlreadyExistsException : AlreadyExistException("User with the specified attributes already exists")
