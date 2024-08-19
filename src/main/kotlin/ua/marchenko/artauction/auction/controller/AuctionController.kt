package ua.marchenko.artauction.auction.controller

import org.springframework.web.bind.annotation.*
import ua.marchenko.artauction.auction.controller.dto.AuctionRequest
import ua.marchenko.artauction.auction.mapper.toAuctionResponse
import ua.marchenko.artauction.auction.service.AuctionService

@RestController
@RequestMapping("/api/v1/auction")
class AuctionController(private val auctionService: AuctionService) {

    @GetMapping("{id}")
    fun getAuctionById(@PathVariable id: String) = auctionService.findById(id).toAuctionResponse()

    @GetMapping
    fun getAllAuctions() = auctionService.findAll().map { it.toAuctionResponse() }

    @PostMapping
    fun addAuction(@RequestBody auction: AuctionRequest) = auctionService.save(auction)

}
