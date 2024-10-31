package ua.marchenko.artauction.common.mongodb.id

import org.bson.types.ObjectId

fun String.toObjectId() = ObjectId(this)
