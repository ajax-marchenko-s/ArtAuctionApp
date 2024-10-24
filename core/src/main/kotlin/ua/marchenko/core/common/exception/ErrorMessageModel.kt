package ua.marchenko.core.common.exception

import java.time.LocalDateTime

data class ErrorMessageModel(
    val status: Int? = null,
    val message: String? = null,
    val timeStamp: LocalDateTime,
)
