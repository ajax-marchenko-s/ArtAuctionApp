package ua.marchenko.artauction.auction.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.mapper.toAuctionFullResponse
import ua.marchenko.artauction.auction.mapper.toAuctionResponse
import ua.marchenko.artauction.auction.service.AuctionService

@RestController
@RequestMapping("/api/v1/auctions")
class AuctionController(private val auctionService: AuctionService) {

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getAuctionById(@PathVariable id: String) = auctionService.getById(id).toAuctionResponse()

    @GetMapping("{id}/full")
    @ResponseStatus(HttpStatus.OK)
    fun getFullAuctionById(@PathVariable id: String) = auctionService.getFullById(id).toAuctionFullResponse()

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getAllAuctions() = auctionService.getAll().map { it.toAuctionResponse() }

    @GetMapping("/full")
    @ResponseStatus(HttpStatus.OK)
    fun getAllFullAuctions() = auctionService.getFullAll().map { it.toAuctionFullResponse() }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addAuction(@Valid @RequestBody auction: CreateAuctionRequest) = auctionService.save(auction)
}
