package ua.marchenko.artauction.exception

import java.time.LocalDateTime

data class ErrorMessageModel(
    var status: Int? = null,
    var message: String? = null,
    val timeStamp: LocalDateTime
)
