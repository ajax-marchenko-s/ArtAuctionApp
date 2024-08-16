package ua.marchenko.artauction.user.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import ua.marchenko.artauction.common.user.getRandomUser
import ua.marchenko.artauction.user.enums.Role
import ua.marchenko.artauction.user.exception.UserNotFoundException
import ua.marchenko.artauction.user.model.User
import ua.marchenko.artauction.user.repository.UserRepository
import kotlin.test.Test

class UserServiceTest {

    private val mockUserRepository = mock(UserRepository::class.java)

    private val userService: UserService = UserServiceImpl(mockUserRepository)


    @Test
    fun `findAll should return a list of users if there are users`() {
        val users = listOf(getRandomUser(role = Role.ARTIST), getRandomUser(role = Role.BUYER))
        `when`(mockUserRepository.getAll()).thenReturn(users)
        val result = userService.findAll()
        assertEquals(2, result.size)
        assertEquals(users[0].name, result[0].name)
        assertEquals(users[1].name, result[1].name)
    }

    @Test
    fun `findAll should return an empty list of users if there are no users`() {
        val users = listOf<User>()
        `when`(mockUserRepository.getAll()).thenReturn(users)
        val result = userService.findAll()
        assertEquals(0, result.size)
    }

    @Test
    fun `findById should return user by id if user with this id exists`() {
        val id = "1"
        val user = getRandomUser(id = id)
        `when`(mockUserRepository.getByIdOrNull(id)).thenReturn(user)
        val result = userService.findById(id)
        assertEquals(result, user)
    }

    @Test
    fun `findById should throw UserNotFoundException if there is no user with this id`() {
        val id = "1"
        `when`(mockUserRepository.getByIdOrNull(id)).thenReturn(null)
        assertThrows<UserNotFoundException> { userService.findById(id) }
    }

    @Test
    fun `findByEmail should return user by email if user with this email exists`() {
        val email = "test@example.com"
        val user = getRandomUser(email = email)
        `when`(mockUserRepository.getByEmailOrNull(email)).thenReturn(user)
        val result = userService.findByEmail(email)
        assertEquals(result, user)
    }

    @Test
    fun `findByEmail should throw UserNotFoundException if there is no user with this email`() {
        val email = "test@example.com"
        `when`(mockUserRepository.getByEmailOrNull(email)).thenReturn(null)
        assertThrows<UserNotFoundException> { userService.findByEmail(email) }
    }

}