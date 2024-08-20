package user

import getRandomString
import ua.marchenko.artauction.user.enums.Role
import ua.marchenko.artauction.user.model.User

fun getRandomUser(
    id: String = getRandomString(),
    role: Role? = Role.ARTIST,
    email: String? = getRandomString(10),
): User {
    return User(
        id = id,
        name = getRandomString(),
        lastName = getRandomString(),
        email = email,
        password = getRandomString(),
        role = role,
    )
}
