package ua.marchenko.artauction.domainservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ArtauctionApplication

fun main(args: Array<String>) {
    @Suppress("SpreadOperator")
    runApplication<ArtauctionApplication>(*args)
}
