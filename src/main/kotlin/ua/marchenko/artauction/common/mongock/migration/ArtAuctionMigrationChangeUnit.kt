package ua.marchenko.artauction.common.mongock.migration

import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index
import org.springframework.data.mongodb.core.index.IndexOperations
import ua.marchenko.artauction.user.model.MongoUser

@ChangeUnit(id = "ArtAuctionMigrationChangeUnit", order = "001", author = "svitlana marchenko", systemVersion = "1")
class ArtAuctionMigrationChangeUnit {

    @Execution
    fun createIndex(mongoTemplate: MongoTemplate) {
        val indexOps: IndexOperations = mongoTemplate.indexOps(MongoUser.COLLECTION)
        indexOps.ensureIndex(
            Index().on(MongoUser::email.name, Sort.Direction.ASC).unique()
        )
        log.info("Index for {} collection for {} field was created", MongoUser.COLLECTION, MongoUser::email.name)
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
        private val log = LoggerFactory.getLogger(ArtAuctionMigrationChangeUnit::class.java)
    }
}
