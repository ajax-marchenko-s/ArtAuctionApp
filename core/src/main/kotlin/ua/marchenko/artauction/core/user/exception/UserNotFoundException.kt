package ua.marchenko.artauction.core.user.exception

import ua.marchenko.artauction.core.common.exception.NotFoundException

class UserNotFoundException(field: String = "ID", value: String) :
    NotFoundException("User with $field $value not found")
