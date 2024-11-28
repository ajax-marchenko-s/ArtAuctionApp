package ua.marchenko.artauction.domainservice.user.infrastructure

import ua.marchenko.artauction.domainservice.user.getRandomString
import ua.marchenko.artauction.domainservice.user.infrastructure.rest.dto.CreateUserRequest

fun CreateUserRequest.Companion.random() =
    CreateUserRequest(name = getRandomString(), lastname = getRandomString(), email = getRandomString())
