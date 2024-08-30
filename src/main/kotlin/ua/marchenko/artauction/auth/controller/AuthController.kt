package ua.marchenko.artauction.auth.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import ua.marchenko.artauction.auth.controller.dto.AuthenticationRequest
import ua.marchenko.artauction.auth.controller.dto.RegistrationRequest
import ua.marchenko.artauction.auth.service.AuthenticationService

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authenticationService: AuthenticationService
) {

    @PostMapping("/authenticate")
    @ResponseStatus(HttpStatus.OK)
    fun authenticate(@Valid @RequestBody authRequest: AuthenticationRequest) =
        authenticationService.authentication(authRequest)

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerUser(@Valid @RequestBody registrationRequest: RegistrationRequest) =
        authenticationService.register(registrationRequest)
}
