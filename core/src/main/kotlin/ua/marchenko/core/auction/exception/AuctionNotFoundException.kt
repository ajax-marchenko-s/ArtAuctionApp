package ua.marchenko.core.auction.exception

import ua.marchenko.core.common.exception.NotFoundException

class AuctionNotFoundException(
    private val auctionId: String? = null,
    message: String = auctionId?.let { "Auction with ID $it not found" } ?: "Auction not found"
) : NotFoundException(message)
