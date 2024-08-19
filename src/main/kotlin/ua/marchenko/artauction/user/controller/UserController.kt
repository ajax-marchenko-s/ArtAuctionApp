package ua.marchenko.artauction.user.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ua.marchenko.artauction.user.mapper.toUserResponse
import ua.marchenko.artauction.user.service.UserService

@RestController
@RequestMapping("/api/v1/users")
class UserController(private val userService: UserService) {

    @GetMapping("{id}")
    fun getUserById(@PathVariable id: String) = userService.findById(id).toUserResponse()

    @GetMapping
    fun getAllUsers() = userService.findAll().map { it.toUserResponse() }
}
