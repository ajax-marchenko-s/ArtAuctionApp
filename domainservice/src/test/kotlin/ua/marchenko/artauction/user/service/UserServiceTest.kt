package ua.marchenko.artauction.user.service

import getRandomEmail
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlin.test.Test
import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import ua.marchenko.artauction.user.exception.UserNotFoundException
import ua.marchenko.artauction.user.model.MongoUser
import ua.marchenko.artauction.user.repository.UserRepository
import ua.marchenko.core.user.enums.Role
import user.random

class UserServiceTest {

    @MockK
    private lateinit var mockUserRepository: UserRepository

    @InjectMockKs
    private lateinit var userService: UserServiceImpl

    @Test
    fun `should return a list of users when users are exist`() {
        // GIVEN
        val users = listOf(MongoUser.random(role = Role.ARTIST), MongoUser.random(role = Role.BUYER))
        every { mockUserRepository.findAll() } returns users.toFlux()

        // WHEN
        val result = userService.getAll()

        // THEN
        result.test()
            .expectNext(users[0], users[1])
            .verifyComplete()
    }

    @Test
    fun `should return an empty list of users when there are no users`() {
        // GIVEN
        every { mockUserRepository.findAll() } returns Flux.empty()

        // WHEN
        val result = userService.getAll()

        // THEN
        result.test()
            .verifyComplete()
    }

    @Test
    fun `should return user by id when user with this id exists`() {
        // GIVEN
        val id = ObjectId().toHexString()
        val user = MongoUser.random(id = id)

        every { mockUserRepository.findById(id) } returns user.toMono()

        // WHEN
        val result = userService.getById(id)

        // THEN
        result.test()
            .expectNext(user)
            .verifyComplete()
    }

    @Test
    fun `should throw UserNotFoundException when there is no user with this id`() {
        // GIVEN
        every { mockUserRepository.findById(any()) } returns Mono.empty()

        // WHEN
        val result = userService.getById(ObjectId().toHexString())

        // THEN
        result.test()
            .verifyError(UserNotFoundException::class.java)
    }

    @Test
    fun `should return user by email when user with this email exists`() {
        // GIVEN
        val email = getRandomEmail()
        val user = MongoUser.random(email = email)

        every { mockUserRepository.findByEmail(email) } returns user.toMono()

        // WHEN
        val result = userService.getByEmail(email)

        // THEN
        result.test()
            .expectNext(user)
            .verifyComplete()
    }

    @Test
    fun `should throw UserNotFoundException when there is no user with this email`() {
        // GIVEN
        val email = getRandomEmail()
        every { mockUserRepository.findByEmail(email) } returns Mono.empty()

        // WHEN
        val result = userService.getByEmail(email)

        // THEN
        result.test()
            .verifyError(UserNotFoundException::class.java)
    }
}
