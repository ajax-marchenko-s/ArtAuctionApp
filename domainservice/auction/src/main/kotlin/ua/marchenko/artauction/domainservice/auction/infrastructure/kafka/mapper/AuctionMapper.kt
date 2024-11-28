package ua.marchenko.artauction.domainservice.auction.infrastructure.kafka.mapper

import java.time.Clock
import java.time.LocalDateTime
import ua.marchenko.artauction.domainservice.auction.domain.Auction
import ua.marchenko.artauction.domainservice.auction.infrastructure.common.mapper.toAuctionProto
import ua.marchenko.artauction.domainservice.auction.infrastructure.common.mapper.toDomain
import ua.marchenko.artauction.domainservice.auction.infrastructure.common.mapper.toLocalDateTime
import ua.marchenko.artauction.domainservice.auction.infrastructure.common.mapper.toTimestampProto
import ua.marchenko.artauction.domainservice.auction.infrastructure.kafka.event.AuctionCreatedEvent
import ua.marchenko.internal.output.pubsub.auction.AuctionCreatedEvent as AuctionCreatedEventProto

fun Auction.toAuctionCreatedEventProto(clock: Clock): AuctionCreatedEventProto {
    return AuctionCreatedEventProto.newBuilder().also {
        it.auction = toAuctionProto(clock)
        it.timestamp = LocalDateTime.now(clock).toTimestampProto(clock)
    }.build()
}

fun AuctionCreatedEventProto.toAuctionCreatedEvent(clock: Clock): AuctionCreatedEvent {
    return AuctionCreatedEvent(auction.toDomain(clock), timestamp.toLocalDateTime(clock))
}
