package ua.marchenko.artauction.auction.mapper

import com.google.protobuf.Timestamp as TimestampProto
import java.time.LocalDateTime
import java.time.ZoneOffset
import ua.marchenko.artauction.auction.domain.AuctionCreatedEvent
import ua.marchenko.artauction.auction.model.MongoAuction
import ua.marchenko.internal.output.pubsub.auction.AuctionCreatedEvent as AuctionCreatedEventProto

fun MongoAuction.toAuctionCreatedEventProto(): AuctionCreatedEventProto {
    return AuctionCreatedEventProto.newBuilder().also { builder ->
        builder.auction = toAuctionProto()
        builder.timestamp = LocalDateTime.now().toTimestampProto()
    }.build()
}

fun AuctionCreatedEventProto.toAuctionCreatedEvent(): AuctionCreatedEvent {
    return AuctionCreatedEvent(
        auction = auction.toMongoAuction(),
        timestamp = timestamp.toLocalDateTime()
    )
}

fun LocalDateTime.toTimestampProto(): TimestampProto {
    val instant = toInstant(ZoneOffset.UTC)
    return TimestampProto.newBuilder()
        .setSeconds(instant.epochSecond)
        .setNanos(instant.nano)
        .build()
}

fun TimestampProto.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofEpochSecond(seconds, nanos, ZoneOffset.UTC)
}
