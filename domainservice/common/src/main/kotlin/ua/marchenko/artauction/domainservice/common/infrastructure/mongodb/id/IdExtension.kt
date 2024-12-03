package ua.marchenko.artauction.domainservice.common.infrastructure.mongodb.id

import org.bson.types.ObjectId

fun String.toObjectId() = ObjectId(this)
