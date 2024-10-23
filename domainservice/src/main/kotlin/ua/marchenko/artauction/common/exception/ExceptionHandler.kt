package ua.marchenko.artauction.common.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ControllerAdvice
import ua.marchenko.artauction.common.exception.type.general.AlreadyExistException
import java.time.LocalDateTime
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.server.ServerWebInputException
import ua.marchenko.artauction.auction.exception.InvalidAuctionOperationException
import ua.marchenko.core.common.exception.ErrorMessageModel
import ua.marchenko.core.common.exception.NotFoundException

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler
    fun handleJsonParseException(ex: HttpMessageNotReadableException) = ResponseEntity(
        createErrorMessageModel(HttpStatus.BAD_REQUEST.value(), "JSON parse error: ${ex.message}"),
        HttpStatus.BAD_REQUEST
    )

    @ExceptionHandler
    fun handleInvalidAuctionOperationException(ex: InvalidAuctionOperationException) =
        ResponseEntity(createErrorMessageModel(HttpStatus.BAD_REQUEST.value(), ex.message), HttpStatus.BAD_REQUEST)

    @ExceptionHandler
    fun handleNotFoundException(ex: NotFoundException) =
        ResponseEntity(createErrorMessageModel(HttpStatus.NOT_FOUND.value(), ex.message), HttpStatus.NOT_FOUND)

    @ExceptionHandler
    fun handleAlreadyExistExceptionException(ex: AlreadyExistException) =
        ResponseEntity(createErrorMessageModel(HttpStatus.CONFLICT.value(), ex.message), HttpStatus.CONFLICT)

    @ExceptionHandler
    fun handleServerWebInputException(ex: ServerWebInputException) = ResponseEntity(
        createErrorMessageModel(HttpStatus.BAD_REQUEST.value(), "JSON parse error: ${ex.message}"),
        HttpStatus.BAD_REQUEST
    )

    @ExceptionHandler
    fun handleException(ex: Exception) = ResponseEntity(
        createErrorMessageModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.message),
        HttpStatus.INTERNAL_SERVER_ERROR
    )

    private fun createErrorMessageModel(status: Int?, message: String?) =
        ErrorMessageModel(status, message, LocalDateTime.now())
}
