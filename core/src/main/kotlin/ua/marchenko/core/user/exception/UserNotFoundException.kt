package ua.marchenko.core.user.exception

import ua.marchenko.core.common.exception.NotFoundException

class UserNotFoundException : NotFoundException {
    constructor(field: String = "ID", value: String) : super("User with $field $value not found")
    constructor(vararg fields: Pair<String, String>) : super(
        "User with " + fields.joinToString(" and ") { "${it.first} ${it.second}" } + " not found"
    )
}
