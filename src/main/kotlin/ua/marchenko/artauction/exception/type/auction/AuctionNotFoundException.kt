package ua.marchenko.artauction.exception.type.auction

import ua.marchenko.artauction.exception.type.general.NotFoundException

class AuctionNotFoundException(private val auctionId: String) : NotFoundException("Auction with ID $auctionId not found")
