package ua.marchenko.artauction.exception.type.user

import ua.marchenko.artauction.exception.type.general.NotFoundException

class UserNotFoundException(userId: String) : NotFoundException("User with ID $userId not found")
