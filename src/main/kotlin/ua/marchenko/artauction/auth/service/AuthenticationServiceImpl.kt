package ua.marchenko.artauction.auth.service

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ua.marchenko.artauction.auth.jwt.JwtService
import ua.marchenko.artauction.auth.controller.dto.AuthenticationRequest
import ua.marchenko.artauction.auth.controller.dto.AuthenticationResponse
import ua.marchenko.artauction.auth.controller.dto.RegistrationRequest
import ua.marchenko.artauction.user.exception.UserAlreadyExistsException
import ua.marchenko.artauction.auth.mapper.toUser
import ua.marchenko.artauction.user.mapper.toUserResponse
import ua.marchenko.artauction.user.repository.UserRepository

@Service
class AuthenticationServiceImpl(
    private val authManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService,
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : AuthenticationService {

    override fun authentication(authenticationRequest: AuthenticationRequest): AuthenticationResponse {
        authManager.authenticate(
            UsernamePasswordAuthenticationToken(
                authenticationRequest.email,
                authenticationRequest.password
            )
        )
        val user = userDetailsService.loadUserByUsername(authenticationRequest.email)
        val accessToken = jwtService.generate(user)
        return AuthenticationResponse(
            accessToken = accessToken,
        )
    }

    override fun register(registrationRequest: RegistrationRequest): AuthenticationResponse {
        if (userRepository.existsByEmail(registrationRequest.email)) {
            throw UserAlreadyExistsException(userEmail = registrationRequest.email)
        }
        val newUser = registrationRequest.copy(password = passwordEncoder.encode(registrationRequest.password))
        val user = userRepository.save(newUser.toUser()).toUserResponse()
        return authentication(AuthenticationRequest(user.email, registrationRequest.password))
    }

}
