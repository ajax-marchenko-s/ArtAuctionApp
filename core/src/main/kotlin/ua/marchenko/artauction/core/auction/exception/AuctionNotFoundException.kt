package ua.marchenko.artauction.core.auction.exception

import ua.marchenko.artauction.core.common.exception.NotFoundException

class AuctionNotFoundException(
    private val auctionId: String? = null,
    message: String = auctionId?.let { "Auction with ID $it not found" } ?: "Auction not found"
) : NotFoundException(message)
