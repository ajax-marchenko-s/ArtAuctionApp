package ua.marchenko.artauction.user.exception

import ua.marchenko.core.common.exception.NotFoundException

class UserNotFoundException(value: String, field: String = "ID") :
    NotFoundException("User with $field $value not found")
