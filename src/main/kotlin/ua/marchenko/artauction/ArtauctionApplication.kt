package ua.marchenko.artauction

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ArtauctionApplication

fun main(args: Array<String>) {
    runApplication<ArtauctionApplication>(*args)
}
