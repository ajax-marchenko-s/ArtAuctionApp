package ua.marchenko.artauction.common.exception

import java.time.LocalDateTime

data class ErrorMessageModel(
    var status: Int? = null,
    var message: String? = null,
    val timeStamp: LocalDateTime,
)
