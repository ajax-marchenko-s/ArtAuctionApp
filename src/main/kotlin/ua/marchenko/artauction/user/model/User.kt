package ua.marchenko.artauction.user.model

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import ua.marchenko.artauction.user.enums.Role

@Document(collection = "user")
data class User(
    @MongoId
    val id: ObjectId? = null,
    val name: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val password: String? = null,
    val role: Role? = null,
) {
    companion object
}
