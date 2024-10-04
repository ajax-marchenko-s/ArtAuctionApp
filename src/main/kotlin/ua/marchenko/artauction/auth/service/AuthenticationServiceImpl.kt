package ua.marchenko.artauction.auth.service

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ua.marchenko.artauction.auth.jwt.JwtService
import ua.marchenko.artauction.auth.controller.dto.AuthenticationRequest
import ua.marchenko.artauction.auth.controller.dto.AuthenticationResponse
import ua.marchenko.artauction.auth.controller.dto.RegistrationRequest
import ua.marchenko.artauction.auth.mapper.toMongo
import ua.marchenko.artauction.user.exception.UserAlreadyExistsException
import ua.marchenko.artauction.user.repository.UserRepository

@Service
class AuthenticationServiceImpl(
    private val authManager: AuthenticationManager,
    private val userDetailsService: ReactiveUserDetailsService,
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : AuthenticationService {

    override fun authentication(authenticationRequest: AuthenticationRequest): Mono<AuthenticationResponse> {
        authManager.authenticate(
            UsernamePasswordAuthenticationToken(
                authenticationRequest.email,
                authenticationRequest.password
            )
        )
        return userDetailsService.findByUsername(authenticationRequest.email)
            .flatMap { user ->
                val accessToken = jwtService.generate(user)
                Mono.just(
                    AuthenticationResponse(
                        accessToken = accessToken,
                    )
                )
            }
    }

    override fun register(registrationRequest: RegistrationRequest): Mono<AuthenticationResponse> {
        return userRepository.existsByEmail(registrationRequest.email)
            .flatMap { existByEmail ->
                if (existByEmail) {
                    Mono.error(UserAlreadyExistsException(userEmail = registrationRequest.email))
                } else {
                    val newUser =
                        registrationRequest.copy(password = passwordEncoder.encode(registrationRequest.password))
                    userRepository.save(newUser.toMongo())
                        .then(
                            authentication(
                                AuthenticationRequest(
                                    registrationRequest.email,
                                    registrationRequest.password
                                )
                            )
                        )
                }
            }
    }
}
