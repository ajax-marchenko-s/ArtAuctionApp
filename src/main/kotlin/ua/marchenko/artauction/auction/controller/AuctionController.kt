package ua.marchenko.artauction.auction.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import ua.marchenko.artauction.auction.controller.dto.CreateAuctionRequest
import ua.marchenko.artauction.auction.mapper.toFullResponse
import ua.marchenko.artauction.auction.mapper.toResponse
import ua.marchenko.artauction.auction.service.AuctionService

@RestController
@RequestMapping("/api/v1/auctions")
class AuctionController(private val auctionService: AuctionService) {

    @GetMapping("{id}")
    fun getAuctionById(@PathVariable id: String) = auctionService.getById(id).toResponse()

    @GetMapping("{id}/full")
    fun getFullAuctionById(@PathVariable id: String) = auctionService.getFullById(id).toFullResponse()

    @GetMapping
    fun getAllAuctions(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ) = auctionService.getAll(page, limit).map { it.toResponse() }

    @GetMapping("/full")
    fun getAllFullAuctions(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") limit: Int
    ) = auctionService.getFullAll(page, limit).map { it.toFullResponse() }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addAuction(@Valid @RequestBody auction: CreateAuctionRequest) = auctionService.save(auction)
}
