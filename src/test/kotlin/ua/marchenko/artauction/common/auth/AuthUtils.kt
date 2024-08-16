package ua.marchenko.artauction.common.auth

import ua.marchenko.artauction.auth.controller.dto.AuthenticationRequest
import ua.marchenko.artauction.auth.controller.dto.RegistrationRequest
import ua.marchenko.artauction.common.getRandomString
import ua.marchenko.artauction.user.enums.Role

fun getRandomRegistrationRequest(): RegistrationRequest {
    return RegistrationRequest(
        email = getRandomString(),
        password = getRandomString(),
        name = getRandomString(),
        lastname = getRandomString(),
        role = Role.ARTIST
    )
}

fun getRandomAuthenticationRequest(): AuthenticationRequest {
    return AuthenticationRequest(getRandomString(), getRandomString())
}