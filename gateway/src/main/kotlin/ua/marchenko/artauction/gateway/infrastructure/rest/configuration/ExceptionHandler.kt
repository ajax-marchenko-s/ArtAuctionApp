package ua.marchenko.artauction.gateway.infrastructure.rest.configuration

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ControllerAdvice
import java.time.LocalDateTime
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import ua.marchenko.artauction.core.common.exception.ErrorMessageModel
import ua.marchenko.artauction.core.common.exception.NotFoundException

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler
    fun handleJsonParseException(ex: HttpMessageNotReadableException) = ResponseEntity(
        createErrorMessageModel(HttpStatus.BAD_REQUEST.value(), "JSON parse error: ${ex.message}"),
        HttpStatus.BAD_REQUEST
    )

    @ExceptionHandler
    fun handleNotFoundException(ex: NotFoundException) =
        ResponseEntity(createErrorMessageModel(HttpStatus.NOT_FOUND.value(), ex.message), HttpStatus.NOT_FOUND)

    @ExceptionHandler
    fun handleMethodArgumentNotValidExceptionException(ex: MethodArgumentNotValidException):
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
    fun handleException(ex: Exception): ResponseEntity<ErrorMessageModel> = ResponseEntity(
        createErrorMessageModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.message),
        HttpStatus.INTERNAL_SERVER_ERROR
    )

    private fun createErrorMessageModel(status: Int?, message: String?) =
        ErrorMessageModel(status, message, LocalDateTime.now())
}
