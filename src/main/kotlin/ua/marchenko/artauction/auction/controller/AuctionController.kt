package ua.marchenko.artauction.auction.controller

import org.springframework.web.bind.annotation.*
import ua.marchenko.artauction.auction.controller.dto.AuctionRequest
import ua.marchenko.artauction.auction.controller.dto.AuctionResponse
import ua.marchenko.artauction.auction.mapper.toAuctionResponse
import ua.marchenko.artauction.auction.service.AuctionService

@RestController
@RequestMapping("/api/v1/auction")
class AuctionController(private val auctionService: AuctionService) {

    @GetMapping("{id}")
    fun getAuctionById(@PathVariable id: String): AuctionResponse {
        return auctionService.findById(id).toAuctionResponse()
    }

    @GetMapping
    fun getAllAuctions(): List<AuctionResponse> {
        return auctionService.findAll().map { it.toAuctionResponse() }
    }

    @PostMapping
    fun addAuction(@RequestBody auction: AuctionRequest): AuctionResponse {
        return auctionService.save(auction)
    }
}