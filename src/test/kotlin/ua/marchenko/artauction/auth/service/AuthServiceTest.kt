package ua.marchenko.artauction.auth.service

import auth.random
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
import getRandomObjectId
import getRandomString
import ua.marchenko.artauction.user.enums.Role
import ua.marchenko.artauction.user.exception.UserAlreadyExistsException
import ua.marchenko.artauction.user.repository.UserRepository
import kotlin.test.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import ua.marchenko.artauction.auth.controller.dto.AuthenticationRequest
import ua.marchenko.artauction.auth.controller.dto.RegistrationRequest

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
        val authenticationRequest = AuthenticationRequest.random()
        val usernamePasswordAuthenticationToken =
            UsernamePasswordAuthenticationToken(authenticationRequest.email, authenticationRequest.password)
        val userDetails = CustomUserDetails(getRandomString(), getRandomString(), Role.ARTIST)
        val expectedResponse = AuthenticationResponse(getRandomString())

        whenever(mockUserDetailsService.loadUserByUsername(authenticationRequest.email)) doReturn (userDetails)
        whenever(mockJwtService.generate(userDetails)) doReturn (expectedResponse.accessToken)

        //WHEN
        val result = authenticationService.authentication(authenticationRequest)

        //THEN
        verify(mockAuthManager).authenticate(usernamePasswordAuthenticationToken)
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `authentication should throw RuntimeException if provided credentials are incorrect`() {
        //GIVEN
        val authenticationRequest = AuthenticationRequest.random()
        val usernamePasswordAuthenticationToken =
            UsernamePasswordAuthenticationToken(authenticationRequest.email, authenticationRequest.password)

        whenever(mockAuthManager.authenticate(usernamePasswordAuthenticationToken)) doThrow (RuntimeException("Unauthorized"))

        //WHEN-THEN
        assertThrows<RuntimeException> { authenticationService.authentication(authenticationRequest) }
    }

    @Test
    fun `register should create user and return access token if user with provided email doesnt exist`() {
        //GIVEN
        val registrationRequest = RegistrationRequest.random()
        val encodedPassword = getRandomString()
        val savedUser =
            registrationRequest.toUser().copy(password = encodedPassword, id = getRandomObjectId())
        val userDetails =
            CustomUserDetails(registrationRequest.email, registrationRequest.password, registrationRequest.role)
        val expectedResponse = AuthenticationResponse(getRandomString())

        whenever(mockUserRepository.existsByEmail(registrationRequest.email)) doReturn (false)
        whenever(mockPasswordEncoder.encode(registrationRequest.password)) doReturn (encodedPassword)
        whenever(mockUserRepository.save(savedUser.copy(id = null))) doReturn (savedUser)
        whenever(mockUserDetailsService.loadUserByUsername(registrationRequest.email)) doReturn (userDetails)
        whenever(mockJwtService.generate(userDetails)) doReturn (expectedResponse.accessToken)

        //WHEN
        val result = authenticationService.register(registrationRequest)

        //THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `register should throw UserAlreadyExistsException if user with email is exist`() {
        //GIVEN
        val registrationRequest = RegistrationRequest.random()
        whenever(mockUserRepository.existsByEmail(registrationRequest.email)) doReturn (true)

        //WHEN-THEN
        assertThrows<UserAlreadyExistsException> { authenticationService.register(registrationRequest) }
    }
}
