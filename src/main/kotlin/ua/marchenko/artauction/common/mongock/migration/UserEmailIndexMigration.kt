package ua.marchenko.artauction.common.mongock.migration

import com.mongodb.client.MongoDatabase
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.bson.Document
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.IndexOperations
import ua.marchenko.artauction.user.model.MongoUser


@ChangeUnit(id = "UserEmailIndexMigration", order = "001", author = "Svitlana Marchenko", systemVersion = "1")
class UserEmailIndexMigration {

    @Execution
    fun createHashedIndex(db: MongoDatabase) {
        db.getCollection(MongoUser.COLLECTION).createIndex(
            Document(MongoUser::email.name, "hashed")
        )
        log.info("Hashed Index for {} collection for {} field was created", MongoUser.COLLECTION, MongoUser::email.name)
    }

    @RollbackExecution
    fun rollbackIndex(mongoTemplate: MongoTemplate) {
        val indexOps: IndexOperations = mongoTemplate.indexOps(MongoUser.COLLECTION)
        if (indexOps.indexInfo.any { it.name == "email_1" }) {
            indexOps.dropIndex("email_1")
            log.info(
                "Index for {} collection for {} field was rolled back",
                MongoUser.COLLECTION,
                MongoUser::email.name
            )
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserEmailIndexMigration::class.java)
    }
}
