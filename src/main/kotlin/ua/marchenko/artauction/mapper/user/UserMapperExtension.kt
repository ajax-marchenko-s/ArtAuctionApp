package ua.marchenko.artauction.mapper.user

import ua.marchenko.artauction.dto.user.UserRequest
import ua.marchenko.artauction.dto.user.UserResponse
import ua.marchenko.artauction.model.User

fun User.toUserResponse() = UserResponse(id, username, email, password, role)

fun UserRequest.toUser() = User(id, username, email, password, role)

fun UserResponse.toUser() = User(id, username, email, password, role)
