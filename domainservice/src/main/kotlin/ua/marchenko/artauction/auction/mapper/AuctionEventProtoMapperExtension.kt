package ua.marchenko.artauction.auction.mapper

import java.time.Clock
import java.time.Instant
import com.google.protobuf.Timestamp as TimestampProto
import java.time.LocalDateTime
import ua.marchenko.artauction.auction.domain.AuctionCreatedEvent
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.internal.output.pubsub.auction.AuctionCreatedEvent as AuctionCreatedEventProto

fun MongoAuction.toAuctionCreatedEventProto(clock: Clock): AuctionCreatedEventProto {
    return AuctionCreatedEventProto.newBuilder().also {
        it.auction = toAuctionProto(clock)
        it.timestamp = LocalDateTime.now(clock).toTimestampProto(clock)
    }.build()
}

fun AuctionCreatedEventProto.toAuctionCreatedEvent(clock: Clock): AuctionCreatedEvent {
    return AuctionCreatedEvent(auction.toMongoAuction(clock), timestamp.toLocalDateTime(clock))
}

fun LocalDateTime.toTimestampProto(clock: Clock): TimestampProto {
    val instant = atZone(clock.zone).toInstant()
    return TimestampProto.newBuilder().also {
        it.seconds = instant.epochSecond
        it.nanos = instant.nano
    }.build()
}

fun TimestampProto.toLocalDateTime(clock: Clock): LocalDateTime {
    return Instant.ofEpochSecond(seconds, nanos.toLong()).atZone(clock.zone).toLocalDateTime()
}
