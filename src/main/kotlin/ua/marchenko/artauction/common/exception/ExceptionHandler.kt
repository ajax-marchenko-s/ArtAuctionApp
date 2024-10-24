package ua.marchenko.artauction.common.exception

import io.jsonwebtoken.ExpiredJwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import ua.marchenko.artauction.common.exception.type.general.AlreadyExistException
import ua.marchenko.artauction.common.exception.type.general.NotFoundException
import java.time.LocalDateTime
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.FieldError
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebInputException
import ua.marchenko.artauction.auction.exception.InvalidAuctionOperationException

@ControllerAdvice
@Suppress("TooManyFunctions")
class ExceptionHandler {

    @ExceptionHandler
    fun handleExpiredJwtException(@Suppress("UnusedParameter") ex: ExpiredJwtException) = ResponseEntity(
        createErrorMessageModel(HttpStatus.UNAUTHORIZED.value(), "The token has expired."),
        HttpStatus.UNAUTHORIZED
    )

    @ExceptionHandler
    fun handleAuthenticationException(ex: AuthenticationException) = ResponseEntity(
        createErrorMessageModel(HttpStatus.UNAUTHORIZED.value(), ex.message ?: "Authentication failed"),
        HttpStatus.UNAUTHORIZED
    )

    @ExceptionHandler
    fun handleServerWebInputException(ex: ServerWebInputException) = ResponseEntity(
        createErrorMessageModel(HttpStatus.BAD_REQUEST.value(), "JSON parse error: ${ex.message}"),
        HttpStatus.BAD_REQUEST
    )

    @ExceptionHandler
    fun handleInvalidAuctionOperationException(ex: InvalidAuctionOperationException) =
        ResponseEntity(createErrorMessageModel(HttpStatus.BAD_REQUEST.value(), ex.message), HttpStatus.BAD_REQUEST)

    @ExceptionHandler
    fun handleBadCredentialsException(ex: BadCredentialsException) =
        ResponseEntity(createErrorMessageModel(HttpStatus.BAD_REQUEST.value(), ex.message), HttpStatus.BAD_REQUEST)

    @ExceptionHandler
    fun handleNotFoundException(ex: NotFoundException) =
        ResponseEntity(createErrorMessageModel(HttpStatus.NOT_FOUND.value(), ex.message), HttpStatus.NOT_FOUND)

    @ExceptionHandler
    fun handleAlreadyExistExceptionException(ex: AlreadyExistException) =
        ResponseEntity(createErrorMessageModel(HttpStatus.CONFLICT.value(), ex.message), HttpStatus.CONFLICT)

    @ExceptionHandler
    fun handleWebExchangeBindException(ex: WebExchangeBindException):
            ResponseEntity<ErrorMessageModel> {
        val message = ex.bindingResult.allErrors.joinToString("; ") { error ->
            "field ${(error as FieldError).field}: ${error.defaultMessage}"
        }
        return ResponseEntity(
            createErrorMessageModel(HttpStatus.BAD_REQUEST.value(), message),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler
    fun handleException(ex: Exception) = ResponseEntity(
        createErrorMessageModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.message),
        HttpStatus.INTERNAL_SERVER_ERROR
    )

    private fun createErrorMessageModel(status: Int?, message: String?) =
        ErrorMessageModel(status, message, LocalDateTime.now())
}
