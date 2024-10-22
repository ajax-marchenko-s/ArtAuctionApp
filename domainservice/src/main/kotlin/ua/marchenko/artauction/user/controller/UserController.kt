package ua.marchenko.artauction.user.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ua.marchenko.artauction.user.mapper.toResponse
import ua.marchenko.artauction.user.service.UserService
import ua.marchenko.core.user.dto.UserResponse

@RestController
@RequestMapping("/api/v1/users")
class UserController(private val userService: UserService) {

    @GetMapping("{id}")
    fun getUserById(@PathVariable id: String): Mono<UserResponse> = userService.getById(id).map { it.toResponse() }

    @GetMapping
    fun getAllUsers(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ): Flux<UserResponse> = userService.getAll(page, limit).map { it.toResponse() }
}
