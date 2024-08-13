package ua.marchenko.artauction.controller

import org.springframework.web.bind.annotation.*
import ua.marchenko.artauction.dto.auction.AuctionRequest
import ua.marchenko.artauction.dto.auction.AuctionResponse
import ua.marchenko.artauction.service.auction.AuctionService

@RestController
@RequestMapping("/api/v1/auction")
class AuctionController(private val auctionService: AuctionService) {

    @GetMapping("{id}")
    fun getAuctionById(@PathVariable id: String): AuctionResponse {
        return auctionService.findById(id)
    }

    @GetMapping
    fun getAllUsers(): List<AuctionResponse> {
        return auctionService.findAll()
    }

    @PostMapping
    fun addUser( @RequestBody user: AuctionRequest): AuctionResponse {
        return auctionService.save(user)
    }
}