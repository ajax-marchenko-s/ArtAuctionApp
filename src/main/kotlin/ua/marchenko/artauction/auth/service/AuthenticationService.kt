package ua.marchenko.artauction.auth.service

import reactor.core.publisher.Mono
import ua.marchenko.artauction.auth.controller.dto.AuthenticationRequest
import ua.marchenko.artauction.auth.controller.dto.AuthenticationResponse
import ua.marchenko.artauction.auth.controller.dto.RegistrationRequest

interface AuthenticationService {
    fun authentication(authenticationRequest: AuthenticationRequest): Mono<AuthenticationResponse>
    fun register(registrationRequest: RegistrationRequest): Mono<AuthenticationResponse>
}
