package ua.marchenko.artauction.controller

import org.springframework.web.bind.annotation.*
import ua.marchenko.artauction.dto.user.UserRequest
import ua.marchenko.artauction.dto.user.UserResponse
import ua.marchenko.artauction.service.user.UserService

@RestController
@RequestMapping("/api/v1/user")
class UserController(private val userService: UserService) {

    @GetMapping("{id}")
    fun getUserById(@PathVariable id: String): UserResponse {
        return userService.findById(id)
    }

    @GetMapping
    fun getAllUsers(): List<UserResponse> {
        return userService.findAll()
    }

    @PostMapping
    fun addUser( @RequestBody user: UserRequest): UserResponse {
        return userService.save(user)
    }

}