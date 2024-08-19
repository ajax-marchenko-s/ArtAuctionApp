package ua.marchenko.artauction.user.model

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.FieldType
import org.springframework.data.mongodb.core.mapping.MongoId
import ua.marchenko.artauction.user.enums.Role

@Document(collection = "user")
data class User(
    @MongoId(FieldType.OBJECT_ID)
    val id: String? = null,
    val name: String?,
    val lastName: String?,
    val email: String?,
    val password: String?,
    val role: Role?
)