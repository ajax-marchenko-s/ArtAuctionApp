package ua.marchenko.artauction.user.controller

import org.springframework.web.bind.annotation.*
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
