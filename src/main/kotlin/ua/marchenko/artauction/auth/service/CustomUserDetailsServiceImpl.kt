package ua.marchenko.artauction.auth.service

import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ua.marchenko.artauction.auth.mapper.toUserDetails
import ua.marchenko.artauction.common.reactive.switchIfEmpty
import ua.marchenko.artauction.user.repository.UserRepository

@Service
class CustomUserDetailsServiceImpl(
    private val userRepository: UserRepository,
) : ReactiveUserDetailsService {

    override fun findByUsername(email: String): Mono<UserDetails> {
        return userRepository.findByEmail(email).switchIfEmpty { Mono.error(UsernameNotFoundException(email)) }
            .map { it.toUserDetails() }
    }
}
