package ua.marchenko.artauction.domainservice.migration

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.IndexOptions
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.bson.Document
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.IndexOperations
import ua.marchenko.artauction.domainservice.user.infrastructure.mongo.entity.MongoUser

@ChangeUnit(id = "UserEmailIndexMigration", order = "001", author = "Svitlana Marchenko", systemVersion = "1")
class UserEmailIndexMigration {

    @Execution
    fun createIndexes(db: MongoDatabase) {
        db.getCollection(MongoUser.COLLECTION).createIndex(
            Document(MongoUser::email.name, "hashed"),
            IndexOptions().name(EMAIL_HASHED_INDEX)
        )
        log.info("Hashed Index for {} collection for {} field was created", MongoUser.COLLECTION, MongoUser::email.name)

        db.getCollection(MongoUser.COLLECTION).createIndex(
            Document(MongoUser::email.name, 1), IndexOptions().unique(true).name(EMAIL_UNIQUE_INDEX)
        )
        log.info(
            "Uniqued Index for {} collection for {} field was created",
            MongoUser.COLLECTION,
            MongoUser::email.name
        )
    }

    @RollbackExecution
    fun rollbackIndexes(mongoTemplate: MongoTemplate) {
        val indexOps: IndexOperations = mongoTemplate.indexOps(MongoUser.COLLECTION)
        if (indexOps.indexInfo.any { it.name == EMAIL_HASHED_INDEX }) {
            indexOps.dropIndex(EMAIL_HASHED_INDEX)
            log.info(
                "Index for {} collection for {} field hashed was rolled back",
                MongoUser.COLLECTION,
                MongoUser::email.name
            )
        }

        if (indexOps.indexInfo.any { it.name == EMAIL_UNIQUE_INDEX }) {
            indexOps.dropIndex(EMAIL_UNIQUE_INDEX)
            log.info(
                "Index for {} collection for {} field unique was rolled back",
                MongoUser.COLLECTION,
                MongoUser::email.name
            )
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserEmailIndexMigration::class.java)
        private const val EMAIL_HASHED_INDEX = "email_hashed"
        private const val EMAIL_UNIQUE_INDEX = "email_1"
    }
}
