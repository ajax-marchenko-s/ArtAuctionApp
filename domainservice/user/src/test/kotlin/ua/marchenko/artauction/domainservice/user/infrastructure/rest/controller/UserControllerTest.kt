package ua.marchenko.artauction.domainservice.user.infrastructure.rest.controller

import ua.marchenko.artauction.domainservice.user.getRandomString
import ua.marchenko.artauction.core.user.exception.UserNotFoundException
import kotlin.test.Test
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import ua.marchenko.artauction.domainservice.user.application.port.input.UserServiceInputPort
import ua.marchenko.artauction.domainservice.user.domain.User
import ua.marchenko.artauction.domainservice.user.domain.random
import ua.marchenko.artauction.domainservice.user.infrastructure.rest.mapper.toResponse

class UserControllerTest {

    @MockK
    private lateinit var mockUserService: UserServiceInputPort

    @InjectMockKs
    private lateinit var userController: UserController

    @Test
    fun `should return a list of UserResponse`() {
        // GIVEN
        val users = listOf(User.random())
        every { mockUserService.getAll() } returns users.toFlux()

        // WHEN
        val result = userController.getAllUsers(0, 10)

        // THEN
        result.test()
            .expectNext(users[0].toResponse())
            .verifyComplete()
    }

    @Test
    fun `should return an empty list when there are no user`() {
        // GIVEN
        every { mockUserService.getAll() } returns Flux.empty()

        // WHEN
        val result = userController.getAllUsers(0, 10)

        // THEN
        result.test()
            .verifyComplete()
    }

    @Test
    fun `should return user with given id when auction with this id exists`() {
        // GIVEN
        val id = ObjectId().toHexString()
        val user = User.random(id = id)

        every { mockUserService.getById(id) } returns user.toMono()

        // WHEN
        val result = userController.getUserById(id)

        // THEN
        result.test()
            .expectNext(user.toResponse())
            .verifyComplete()
    }

    @Test
    fun `should throw UserNotFoundException when there is no user with this id`() {
        // GIVEN
        val id = getRandomString()
        every { mockUserService.getById(id) } returns Mono.error(UserNotFoundException(value = id))

        // WHEN
        val result = userController.getUserById(id)

        // THEN
        result.test()
            .verifyError(UserNotFoundException::class.java)
    }
}
