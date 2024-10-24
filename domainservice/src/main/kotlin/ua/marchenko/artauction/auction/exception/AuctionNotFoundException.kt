package ua.marchenko.artauction.auction.exception

import ua.marchenko.core.common.exception.NotFoundException

class AuctionNotFoundException(private val auctionId: String) :
    NotFoundException("Auction with ID $auctionId not found")
