package ua.marchenko.artauction.auth.service

import ua.marchenko.artauction.auth.controller.dto.AuthenticationRequest
import ua.marchenko.artauction.auth.controller.dto.AuthenticationResponse
import ua.marchenko.artauction.auth.controller.dto.RegistrationRequest

interface AuthenticationService {

    fun authentication(authenticationRequest: AuthenticationRequest): AuthenticationResponse

    fun register(registrationRequest: RegistrationRequest): AuthenticationResponse
}
