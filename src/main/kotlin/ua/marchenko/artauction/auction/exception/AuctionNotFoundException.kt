package ua.marchenko.artauction.auction.exception

import ua.marchenko.artauction.common.exception.type.general.NotFoundException

class AuctionNotFoundException(private val auctionId: String) :
    NotFoundException("Auction with ID $auctionId not found")
