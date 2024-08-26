package ua.marchenko.artauction.user.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import ua.marchenko.artauction.user.enums.Role
import ua.marchenko.artauction.user.exception.UserNotFoundException
import ua.marchenko.artauction.user.model.User
import ua.marchenko.artauction.user.repository.UserRepository
import kotlin.test.Test
import getRandomEmail
import getRandomObjectId
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import user.random

class UserServiceTest {

    @MockK
    private lateinit var mockUserRepository: UserRepository

    @InjectMockKs
    private lateinit var userService: UserServiceImpl

    @Test
    fun `should return a list of users when users are exist`() {
        //GIVEN
        val users = listOf(User.random(role = Role.ARTIST), User.random(role = Role.BUYER))
        every { mockUserRepository.findAll() } returns users

        //WHEN
        val result = userService.getAll()

        //THEN
        assertEquals(2, result.size)
        assertEquals(users[0].name, result[0].name)
        assertEquals(users[1].name, result[1].name)
    }

    @Test
    fun `should return an empty list of users when there are no users`() {
        //GIVEN
        val users = listOf<User>()
        every {
            mockUserRepository.findAll()
        } returns users

        //WHEN
        val result = userService.getAll()

        //THEN
        assertEquals(0, result.size)
    }

    @Test
    fun `should return user by id when user with this id exists`() {
        //GIVEN
        val id = getRandomObjectId().toHexString()
        val user = User.random(id = id)

        every { mockUserRepository.findById(id) } returns user

        //WHEN
        val result = userService.getById(id)

        //THEN
        assertEquals(result, user)
    }

    @Test
    fun `should throw UserNotFoundException when there is no user with this id`() {
        //GIVEN
        val id = getRandomObjectId().toHexString()
        every { mockUserRepository.findById(id) } returns null

        //WHEN //THEN
        assertThrows<UserNotFoundException> { userService.getById(id) }
    }

    @Test
    fun `should return user by email when user with this email exists`() {
        //GIVEN
        val email = getRandomEmail()
        val user = User.random(email = email)

        every { mockUserRepository.findByEmail(email) } returns user

        //WHEN
        val result = userService.getByEmail(email)

        //THEN
        assertEquals(result, user)
    }

    @Test
    fun `should throw UserNotFoundException when there is no user with this email`() {
        //GIVEN
        val email = getRandomEmail()
        every { mockUserRepository.findByEmail(email) } returns null

        //WHEN //THEN
        assertThrows<UserNotFoundException> { userService.getByEmail(email) }
    }
}
