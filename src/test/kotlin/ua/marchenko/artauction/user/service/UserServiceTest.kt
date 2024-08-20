package ua.marchenko.artauction.user.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import user.getRandomUser
import ua.marchenko.artauction.user.enums.Role
import ua.marchenko.artauction.user.exception.UserNotFoundException
import ua.marchenko.artauction.user.model.User
import ua.marchenko.artauction.user.repository.UserRepository
import kotlin.test.Test
import getRandomEmail
import getRandomString

class UserServiceTest {

    private val mockUserRepository = mock(UserRepository::class.java)

    private val userService: UserService = UserServiceImpl(mockUserRepository)

    @Test
    fun `getAll should return a list of users if there are users`() {
        //GIVEN
        val users = listOf(getRandomUser(role = Role.ARTIST), getRandomUser(role = Role.BUYER))
        `when`(mockUserRepository.findAll()).thenReturn(users)

        //WHEN
        val result = userService.getAll()

        //THEN
        assertEquals(2, result.size)
        assertEquals(users[0].name, result[0].name)
        assertEquals(users[1].name, result[1].name)
    }

    @Test
    fun `getAll should return an empty list of users if there are no users`() {
        //GIVEN
        val users = listOf<User>()
        `when`(mockUserRepository.findAll()).thenReturn(users)

        //WHEN
        val result = userService.getAll()

        //THEN
        assertEquals(0, result.size)
    }

    @Test
    fun `getById should return user by id if user with this id exists`() {
        //GIVEN
        val id = getRandomString()
        val user = getRandomUser(id = id)

        `when`(mockUserRepository.findById(id)).thenReturn(user)

        //WHEN
        val result = userService.getById(id)

        //THEN
        assertEquals(result, user)
    }

    @Test
    fun `getById should throw UserNotFoundException if there is no user with this id`() {
        //GIVEN
        val id = getRandomString()
        `when`(mockUserRepository.findById(id)).thenReturn(null)

        //WHEN-THEN
        assertThrows<UserNotFoundException> { userService.getById(id) }
    }

    @Test
    fun `getByEmail should return user by email if user with this email exists`() {
        //GIVEN
        val email = getRandomEmail()
        val user = getRandomUser(email = email)

        `when`(mockUserRepository.findByEmail(email)).thenReturn(user)

        //WHEN
        val result = userService.getByEmail(email)

        //THEN
        assertEquals(result, user)
    }

    @Test
    fun `getByEmail should throw UserNotFoundException if there is no user with this email`() {
        //GIVEN
        val email = getRandomEmail()
        `when`(mockUserRepository.findByEmail(email)).thenReturn(null)

        //WHEN-THEN
        assertThrows<UserNotFoundException> { userService.getByEmail(email) }
    }
}
