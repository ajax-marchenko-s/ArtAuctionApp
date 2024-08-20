package ua.marchenko.artauction.auth.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import ua.marchenko.artauction.auth.controller.dto.AuthenticationResponse
import ua.marchenko.artauction.auth.data.CustomUserDetails
import ua.marchenko.artauction.auth.jwt.JwtService
import ua.marchenko.artauction.auth.mapper.toUser
import auth.getRandomAuthenticationRequest
import auth.getRandomRegistrationRequest
import getRandomString
import ua.marchenko.artauction.user.enums.Role
import ua.marchenko.artauction.user.exception.UserAlreadyExistsException
import ua.marchenko.artauction.user.repository.UserRepository
import kotlin.test.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

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
        //GIVEN
        val authenticationRequest = getRandomAuthenticationRequest()
        val usernamePasswordAuthenticationToken =
            UsernamePasswordAuthenticationToken(authenticationRequest.email, authenticationRequest.password)
        val userDetails = CustomUserDetails(getRandomString(), getRandomString(), Role.ARTIST)
        val expectedResponse = AuthenticationResponse(getRandomString())

        `when`(mockUserDetailsService.loadUserByUsername(authenticationRequest.email)).thenReturn(userDetails)
        `when`(mockJwtService.generate(userDetails)).thenReturn(expectedResponse.accessToken)

        //WHEN
        val result = authenticationService.authentication(authenticationRequest)

        //THEN
        verify(mockAuthManager).authenticate(usernamePasswordAuthenticationToken)
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `authentication should throw RuntimeException if provided credentials are incorrect`() {
        //GIVEN
        val authenticationRequest = getRandomAuthenticationRequest()
        val usernamePasswordAuthenticationToken =
            UsernamePasswordAuthenticationToken(authenticationRequest.email, authenticationRequest.password)

        `when`(mockAuthManager.authenticate(usernamePasswordAuthenticationToken))
            .thenThrow(RuntimeException("Unauthorized"))

        //WHEN-THEN
        assertThrows<RuntimeException> { authenticationService.authentication(authenticationRequest) }
    }

    @Test
    fun `register should create user and return access token if user with provided email doesnt exist`() {
        //GIVEN
        val registrationRequest = getRandomRegistrationRequest()
        val encodedPassword = getRandomString()
        val savedUser = registrationRequest.toUser().copy(password = encodedPassword, id = getRandomString())
        val userDetails =
            CustomUserDetails(registrationRequest.email, registrationRequest.password, registrationRequest.role)
        val expectedResponse = AuthenticationResponse(getRandomString())

        `when`(mockUserRepository.existsByEmail(registrationRequest.email)).thenReturn(false)
        `when`(mockPasswordEncoder.encode(registrationRequest.password)).thenReturn(encodedPassword)
        `when`(mockUserRepository.save(savedUser.copy(id = null))).thenReturn(savedUser)
        `when`(mockUserDetailsService.loadUserByUsername(registrationRequest.email)).thenReturn(userDetails)
        `when`(mockJwtService.generate(userDetails)).thenReturn(expectedResponse.accessToken)

        //WHEN
        val result = authenticationService.register(registrationRequest)

        //THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `register should throw UserAlreadyExistsException if user with email is exist`() {
        //GIVEN
        val registrationRequest = getRandomRegistrationRequest()
        `when`(mockUserRepository.existsByEmail(registrationRequest.email)).thenReturn(true)

        //WHEN-THEN
        assertThrows<UserAlreadyExistsException> { authenticationService.register(registrationRequest) }
    }
}
