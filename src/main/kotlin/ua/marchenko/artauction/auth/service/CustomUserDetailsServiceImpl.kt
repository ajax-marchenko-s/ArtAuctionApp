package ua.marchenko.artauction.auth.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import ua.marchenko.artauction.auth.mapper.toUserDetails
import ua.marchenko.artauction.user.repository.UserRepository


@Service
class CustomUserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        return userRepository.findByEmail(email)
            ?.toUserDetails()
            ?: throw UsernameNotFoundException("User not found: $email")
    }
}
