package ua.marchenko.internal

object KafkaTopic {

    private const val REQUEST_PREFIX = "ua.marchenko.artauction.output.pub"

    object AuctionKafkaTopic {
        private const val AUCTION_PREFIX = "$REQUEST_PREFIX.auction"
        const val CREATED = "$AUCTION_PREFIX.created"
    }
}
