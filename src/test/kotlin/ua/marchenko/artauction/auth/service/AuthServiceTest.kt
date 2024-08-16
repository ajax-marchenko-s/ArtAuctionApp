package ua.marchenko.artauction.auth.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import ua.marchenko.artauction.auth.controller.dto.AuthenticationResponse
import ua.marchenko.artauction.auth.data.CustomUserDetails
import ua.marchenko.artauction.auth.jwt.JwtService
import ua.marchenko.artauction.auth.mapper.toUser
import ua.marchenko.artauction.common.auth.getRandomAuthenticationRequest
import ua.marchenko.artauction.common.auth.getRandomRegistrationRequest
import ua.marchenko.artauction.common.getRandomString
import ua.marchenko.artauction.user.enums.Role
import ua.marchenko.artauction.user.exception.UserAlreadyExistsException
import ua.marchenko.artauction.user.repository.UserRepository
import kotlin.test.Test

class AuthServiceTest {

    private val mockAuthManager: AuthenticationManager = mock(AuthenticationManager::class.java)
    private val mockUserDetailsService: UserDetailsService = mock(UserDetailsService::class.java)
    private val mockJwtService: JwtService = mock(JwtService::class.java)
    private val mockUserRepository: UserRepository = mock(UserRepository::class.java)
    private val mockPasswordEncoder: PasswordEncoder = mock(PasswordEncoder::class.java)

    private val authenticationService: AuthenticationService = AuthenticationServiceImpl(
        mockAuthManager,
        mockUserDetailsService,
        mockJwtService,
        mockUserRepository,
        mockPasswordEncoder
    )

    @Test
    fun `authentication should return AuthenticationResponse if provided credentials are correct`() {
        val authenticationRequest = getRandomAuthenticationRequest()
        val usernamePasswordAuthenticationToken =
            UsernamePasswordAuthenticationToken(authenticationRequest.email, authenticationRequest.password)
        val userDetails = CustomUserDetails(getRandomString(), getRandomString(), Role.ARTIST)
        val expectedResponse = AuthenticationResponse("smth_like_token")

        `when`(mockUserDetailsService.loadUserByUsername(authenticationRequest.email)).thenReturn(userDetails)
        `when`(mockJwtService.generate(userDetails)).thenReturn(expectedResponse.accessToken)

        val result = authenticationService.authentication(authenticationRequest)

        verify(mockAuthManager).authenticate(usernamePasswordAuthenticationToken)
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `authentication should throw RuntimeException if provided credentials are incorrect`() {
        val authenticationRequest = getRandomAuthenticationRequest()
        val usernamePasswordAuthenticationToken =
            UsernamePasswordAuthenticationToken(authenticationRequest.email, authenticationRequest.password)
        `when`(mockAuthManager.authenticate(usernamePasswordAuthenticationToken))
            .thenThrow(RuntimeException("Unauthorized"))
        assertThrows<RuntimeException> { authenticationService.authentication(authenticationRequest) }
    }

    @Test
    fun `register should create user and return access token if user with provided email doesnt exist`() {
        val registrationRequest = getRandomRegistrationRequest()
        val encodedPassword = getRandomString()
        val savedUser = registrationRequest.toUser().copy(password = encodedPassword, id = getRandomString())
        val userDetails =
            CustomUserDetails(registrationRequest.email, registrationRequest.password, registrationRequest.role)
        val expectedResponse = AuthenticationResponse("smth_like_token")

        `when`(mockUserRepository.existsByEmail(registrationRequest.email)).thenReturn(false)
        `when`(mockPasswordEncoder.encode(registrationRequest.password)).thenReturn(encodedPassword)
        `when`(mockUserRepository.save(savedUser.copy(id = null))).thenReturn(savedUser)
        `when`(mockUserDetailsService.loadUserByUsername(registrationRequest.email)).thenReturn(userDetails)
        `when`(mockJwtService.generate(userDetails)).thenReturn(expectedResponse.accessToken)

        val result = authenticationService.register(registrationRequest)

        assertEquals(expectedResponse, result)
    }

    @Test
    fun `register should throw UserAlreadyExistsException if user with email is exist`() {
        val registrationRequest = getRandomRegistrationRequest()
        `when`(mockUserRepository.existsByEmail(registrationRequest.email)).thenReturn(true)
        assertThrows<UserAlreadyExistsException> { authenticationService.register(registrationRequest) }
    }

}