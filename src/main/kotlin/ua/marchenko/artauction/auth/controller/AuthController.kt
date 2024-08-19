package ua.marchenko.artauction.auth.controller

import org.springframework.web.bind.annotation.*
import ua.marchenko.artauction.auth.controller.dto.AuthenticationRequest
import ua.marchenko.artauction.auth.controller.dto.RegistrationRequest
import ua.marchenko.artauction.auth.service.AuthenticationService

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authenticationService: AuthenticationService
) {

    @PostMapping("/authenticate")
    fun authenticate(@RequestBody authRequest: AuthenticationRequest) =
        authenticationService.authentication(authRequest)

    @PostMapping("/registration")
    fun registerUser(@RequestBody registrationRequest: RegistrationRequest) =
        authenticationService.register(registrationRequest)

}
