package ua.marchenko.artauction.user.exception

import ua.marchenko.artauction.common.exception.type.general.NotFoundException

class UserNotFoundException(value: String, field: String = "ID") :
    NotFoundException("User with $field $value not found")
