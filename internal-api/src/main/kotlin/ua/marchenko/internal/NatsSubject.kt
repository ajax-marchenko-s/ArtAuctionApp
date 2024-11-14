package ua.marchenko.internal

object NatsSubject {
    object ArtworkNatsSubject {
        private const val ARTWORK_PREFIX = "artwork"
        const val FIND_BY_ID = "$ARTWORK_PREFIX.find_by_id"
        const val FIND_ALL = "$ARTWORK_PREFIX.find_all"
        const val FIND_BY_ID_FULL = "$ARTWORK_PREFIX.find_by_id_full"
        const val FIND_ALL_FULL = "$ARTWORK_PREFIX.find_all_full"
        const val CREATE = "$ARTWORK_PREFIX.create"
    }

    object AuctionNatsSubject {
        private const val AUCTION_PREFIX = "auction"
        const val CREATE = "$AUCTION_PREFIX.create"
        const val FIND_BY_ID = "$AUCTION_PREFIX.find_by_id"
        const val FIND_ALL = "$AUCTION_PREFIX.find_all"
        const val CREATED_EVENT = "$AUCTION_PREFIX.created_event"
    }
}
