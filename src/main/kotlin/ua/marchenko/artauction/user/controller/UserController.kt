package ua.marchenko.artauction.user.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import ua.marchenko.artauction.user.mapper.toUserResponse
import ua.marchenko.artauction.user.service.UserService

@RestController
@RequestMapping("/api/v1/users")
class UserController(private val userService: UserService) {

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getUserById(@PathVariable id: String) = userService.getById(id).toUserResponse()

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getAllUsers() = userService.getAll().map { it.toUserResponse() }
}
