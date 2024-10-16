package auth

import getRandomEmail
import ua.marchenko.artauction.auth.controller.dto.AuthenticationRequest
import ua.marchenko.artauction.auth.controller.dto.RegistrationRequest
import getRandomString
import ua.marchenko.artauction.user.enums.Role

fun RegistrationRequest.Companion.random() = RegistrationRequest(
    email = getRandomEmail(),
    password = getRandomString(),
    name = getRandomString(),
    lastname = getRandomString(),
    role = Role.ARTIST,
)

fun AuthenticationRequest.Companion.random() = AuthenticationRequest(getRandomEmail(), getRandomString())
