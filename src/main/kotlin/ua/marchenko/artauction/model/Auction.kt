package ua.marchenko.artauction.model

import java.time.LocalDateTime
import java.util.*

data class Auction(
    val id: String = UUID.randomUUID().toString(),
    val artwork: Artwork,
    val bid: Double = 0.0,
    val buyer: User? = null,
    val endDate: LocalDateTime
)
