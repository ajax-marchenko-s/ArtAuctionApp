package ua.marchenko.artauction.config.web

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebInputException
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import ua.marchenko.artauction.auction.exception.InvalidAuctionOperationException
import ua.marchenko.artauction.auth.jwt.JwtAuthenticationFilter
import ua.marchenko.artauction.common.exception.ErrorMessageModel
import ua.marchenko.artauction.common.exception.type.general.AlreadyExistException
import ua.marchenko.artauction.common.exception.type.general.NotFoundException

@WebFluxTest(ExceptionHandlerTestController::class)
@Import(value = [ExceptionHandlerTestConfiguration::class])
@AutoConfigureWebTestClient
class ExceptionHandlerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockkBean
    private lateinit var mockTestController: ExceptionHandlerTestController

    @MockkBean
    private lateinit var mockJwtAuthenticationFilter: JwtAuthenticationFilter

    @Test
    fun `should return 404 when controller throw NotFoundException`() {
        // GIVEN
        every { mockTestController.test() } returns Mono.error(NotFoundException(ERROR_MESSAGE))
        every { mockJwtAuthenticationFilter.filter(any(), any()) } answers {
            secondArg<WebFilterChain>().filter(firstArg())
        }

        // WHEN THEN
        webTestClient.get()
            .uri(URL)
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath(ErrorMessageModel::message.name).isEqualTo(ERROR_MESSAGE)
    }

    @Test
    fun `should return 409 when controller throw AlreadyExistException`() {
        // GIVEN
        every { mockTestController.test() } returns Mono.error(AlreadyExistException(ERROR_MESSAGE))
        every { mockJwtAuthenticationFilter.filter(any(), any()) } answers {
            secondArg<WebFilterChain>().filter(firstArg())
        }

        // WHEN THEN
        webTestClient.get()
            .uri(URL)
            .exchange()
            .expectStatus().isEqualTo(409)
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath(ErrorMessageModel::message.name).isEqualTo(ERROR_MESSAGE)
    }

    @Test
    fun `should return 400 when controller throw BadCredentialsException`() {
        // GIVEN
        every { mockTestController.test() } returns Mono.error(BadCredentialsException(ERROR_MESSAGE))
        every { mockJwtAuthenticationFilter.filter(any(), any()) } answers {
            secondArg<WebFilterChain>().filter(firstArg())
        }

        // WHEN THEN
        webTestClient.get()
            .uri(URL)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath(ErrorMessageModel::message.name).isEqualTo(ERROR_MESSAGE)
    }

    @Test
    fun `should return 400 when controller throw ServerWebInputException`() {
        // GIVEN
        every { mockTestController.test() } returns Mono.error(ServerWebInputException(ERROR_MESSAGE))
        every { mockJwtAuthenticationFilter.filter(any(), any()) } answers {
            secondArg<WebFilterChain>().filter(firstArg())
        }

        // WHEN THEN
        webTestClient.get()
            .uri(URL)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath(ErrorMessageModel::message.name)
            .isEqualTo("JSON parse error: 400 BAD_REQUEST \"$ERROR_MESSAGE\"")
    }

    @Test
    fun `should return 400 when controller throw InvalidAuctionOperationException`() {
        // GIVEN
        every { mockTestController.test() } returns
                Mono.error(InvalidAuctionOperationException(ERROR_MESSAGE))
        every { mockJwtAuthenticationFilter.filter(any(), any()) } answers {
            secondArg<WebFilterChain>().filter(firstArg())
        }

        // WHEN THEN
        webTestClient.get()
            .uri(URL)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath(ErrorMessageModel::message.name).isEqualTo(ERROR_MESSAGE)
    }

    @Test
    fun `should return 400 when controller throw WebExchangeBindException`() {
        // GIVEN
        val targetObject = Any()
        val objectName = "authenticationRequest"
        val invalidField = "email"
        val defaultMessage = "Invalid email format"
        val bindingResult: BindingResult = BeanPropertyBindingResult(targetObject, objectName)
        bindingResult.addError(FieldError(objectName, invalidField, defaultMessage))
        val methodParameter = mockk<MethodParameter>(relaxed = true)

        every { mockTestController.test() } returns Mono.error(WebExchangeBindException(methodParameter, bindingResult))
        every { mockJwtAuthenticationFilter.filter(any(), any()) } answers {
            secondArg<WebFilterChain>().filter(firstArg())
        }

        // WHEN THEN
        webTestClient.get()
            .uri(URL)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath(ErrorMessageModel::message.name).isEqualTo("field $invalidField: $defaultMessage")
    }

    @Test
    fun `should return 500 when controller throw Exception`() {
        // GIVEN
        every { mockTestController.test() } returns Mono.error(Exception(ERROR_MESSAGE))
        every { mockJwtAuthenticationFilter.filter(any(), any()) } answers {
            secondArg<WebFilterChain>().filter(firstArg())
        }

        // WHEN THEN
        webTestClient.get()
            .uri(URL)
            .exchange()
            .expectStatus().is5xxServerError
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath(ErrorMessageModel::message.name).isEqualTo(ERROR_MESSAGE)
    }

    companion object {
        private const val URL = "/test"
        private const val ERROR_MESSAGE = "Something went wrong"
    }
}
