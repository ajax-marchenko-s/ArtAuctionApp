package ua.marchenko.artauction.auth.service

import auth.random
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import ua.marchenko.artauction.auth.controller.dto.AuthenticationResponse
import ua.marchenko.artauction.auth.data.CustomUserDetails
import getRandomString
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import ua.marchenko.artauction.user.enums.Role
import ua.marchenko.artauction.user.exception.UserAlreadyExistsException
import ua.marchenko.artauction.user.repository.UserRepository
import kotlin.test.Test
import org.bson.types.ObjectId
import ua.marchenko.artauction.auth.controller.dto.AuthenticationRequest
import ua.marchenko.artauction.auth.controller.dto.RegistrationRequest
import ua.marchenko.artauction.auth.jwt.JwtServiceImpl
import ua.marchenko.artauction.auth.mapper.toMongo

class AuthServiceTest {

    @MockK
    private lateinit var mockAuthManager: AuthenticationManager

    @MockK
    private lateinit var mockUserDetailsService: CustomUserDetailsServiceImpl

    @MockK
    private lateinit var mockJwtService: JwtServiceImpl

    @MockK
    private lateinit var mockUserRepository: UserRepository

    @MockK
    private lateinit var mockPasswordEncoder: PasswordEncoder

    @InjectMockKs
    private lateinit var authenticationService: AuthenticationServiceImpl

    @Test
    fun `should return AuthenticationResponse when provided credentials are correct`() {
        //GIVEN
        val authenticationRequest = AuthenticationRequest.random()
        val usernamePasswordAuthenticationToken =
            UsernamePasswordAuthenticationToken(authenticationRequest.email, authenticationRequest.password)
        val userDetails = CustomUserDetails(getRandomString(), getRandomString(), Role.ARTIST)
        val expectedResponse = AuthenticationResponse(getRandomString())

        every { mockAuthManager.authenticate(usernamePasswordAuthenticationToken) } returns null
        every { mockUserDetailsService.loadUserByUsername(authenticationRequest.email) } returns userDetails
        every { mockJwtService.generate(userDetails) } returns expectedResponse.accessToken

        //WHEN
        val result = authenticationService.authentication(authenticationRequest)

        //THEN
        verify { mockAuthManager.authenticate(usernamePasswordAuthenticationToken) }
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should throw RuntimeException when provided credentials are incorrect`() {
        //GIVEN
        val authenticationRequest = AuthenticationRequest.random()
        val usernamePasswordAuthenticationToken =
            UsernamePasswordAuthenticationToken(authenticationRequest.email, authenticationRequest.password)

        every {
            mockAuthManager.authenticate(usernamePasswordAuthenticationToken)
        } throws RuntimeException("Unauthorized")


        //WHEN //THEN
        assertThrows<RuntimeException> { authenticationService.authentication(authenticationRequest) }
    }

    @Test
    fun `should create user and return access token when user with provided email doesnt exist`() {
        //GIVEN
        val registrationRequest = RegistrationRequest.random()
        val encodedPassword = getRandomString()
        val savedUser =
            registrationRequest.toMongo().copy(password = encodedPassword, id = ObjectId())
        val userDetails =
            CustomUserDetails(registrationRequest.email, registrationRequest.password, registrationRequest.role)
        val expectedResponse = AuthenticationResponse(getRandomString())

        every {
            mockAuthManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    registrationRequest.email,
                    registrationRequest.password
                )
            )
        } returns null
        every { mockUserRepository.existsByEmail(registrationRequest.email) } returns false
        every { mockPasswordEncoder.encode(registrationRequest.password) } returns encodedPassword
        every { mockUserRepository.save(savedUser.copy(id = null)) } returns savedUser
        every { mockUserDetailsService.loadUserByUsername(registrationRequest.email) } returns userDetails
        every { mockJwtService.generate(userDetails) } returns expectedResponse.accessToken

        //WHEN
        val result = authenticationService.register(registrationRequest)

        //THEN
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `should throw UserAlreadyExistsException when user with registration email is already exist`() {
        //GIVEN
        val registrationRequest = RegistrationRequest.random()
        every { mockUserRepository.existsByEmail(registrationRequest.email) } returns true

        //WHEN //THEN
        assertThrows<UserAlreadyExistsException> { authenticationService.register(registrationRequest) }
    }
}
