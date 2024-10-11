package ua.marchenko.artauction.auth.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import ua.marchenko.artauction.auth.jwt.JwtAuthenticationFilter

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration(
    private val reactiveAuthenticationManager: ReactiveAuthenticationManager,
) {

    @Bean
    fun securityFilterChain(
        http: ServerHttpSecurity,
        jwtAuthenticationFilter: JwtAuthenticationFilter,
    ): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .httpBasic { it.disable() }
            .authorizeExchange { authorize ->
                authorize
                    .pathMatchers("/api/v1/auth/*").permitAll()
                    .pathMatchers("/error").permitAll()
                    .pathMatchers(HttpMethod.POST, "/api/v1/artworks/**").hasAuthority("ARTIST")
                    .pathMatchers(HttpMethod.PUT, "/api/v1/artworks/**").hasAuthority("ARTIST")
                    .pathMatchers(HttpMethod.DELETE, "/api/v1/artworks/**").hasAuthority("ARTIST")
                    .pathMatchers(HttpMethod.POST, "/api/v1/auctions/**").hasAuthority("ARTIST")
                    .anyExchange().permitAll()
            }
            .authenticationManager(reactiveAuthenticationManager)
            .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }
}
