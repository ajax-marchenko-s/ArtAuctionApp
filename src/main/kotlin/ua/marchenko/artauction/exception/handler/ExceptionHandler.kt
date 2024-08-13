package ua.marchenko.artauction.exception.handler

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import ua.marchenko.artauction.exception.ErrorMessageModel
import ua.marchenko.artauction.exception.type.general.NotFoundException
import java.time.LocalDateTime

@ControllerAdvice
class ExceptionController {

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: NotFoundException): ResponseEntity<ErrorMessageModel> {
        val errorMessage = ErrorMessageModel(
            HttpStatus.NOT_FOUND.value(),
            ex.message,
            LocalDateTime.now(),
        )
        return ResponseEntity(errorMessage, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(ex: IllegalStateException): ResponseEntity<ErrorMessageModel> {
        val errorMessage = ErrorMessageModel(
            HttpStatus.NOT_FOUND.value(),
            ex.message,
            LocalDateTime.now(),
        )
        return ResponseEntity(errorMessage, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ErrorMessageModel> {
        val errorMessage = ErrorMessageModel(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.message,
            LocalDateTime.now(),
        )
        return ResponseEntity(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}