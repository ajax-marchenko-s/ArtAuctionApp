package ua.marchenko.artauction.config.migration

import kotlin.test.Test
import org.bson.Document
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import ua.marchenko.artauction.common.AbstractBaseIntegrationTest
import ua.marchenko.artauction.common.mongock.migration.UserEmailIndexMigration
import ua.marchenko.artauction.user.model.MongoUser

class UserEmailIndexMigrationTest : AbstractBaseIntegrationTest {

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Test
    fun `should create hashed and unique index for email field`() {
        // WHEN
        UserEmailIndexMigration().createIndexes(mongoTemplate.db)

        //THEN
        val indexes = mongoTemplate.db.getCollection(MongoUser.COLLECTION).listIndexes().toList()
        assertTrue(
            indexes.any {
                it["name"] == EMAIL_HASHED_INDEX
                        && (it["key"] as Document).size == 1
                        && (it["key"] as Document)["email"] == "hashed"
            },
            "Email hashed index must be contained in the database"
        )
        assertTrue(
            indexes.any {
                it["name"] == EMAIL_UNIQUE_INDEX
                        && it["unique"] == true
                        && (it["key"] as Document).size == 1
                        && (it["key"] as Document)["email"] == 1
            },
            "Email unique index must be contained in the database"
        )
    }

    @Test
    fun `should remove created indexes when this indexes exists`() {
        // GIVEN
        UserEmailIndexMigration().createIndexes(mongoTemplate.db)

        // WHEN
        UserEmailIndexMigration().rollbackIndexes(mongoTemplate)

        //THEN
        val indexes = mongoTemplate.db.getCollection(MongoUser.COLLECTION).listIndexes().toList()
        assertFalse(
            indexes.any {
                it["name"] == EMAIL_HASHED_INDEX
                        && (it["key"] as Document).size == 1
                        && (it["key"] as Document)["email"] == "hashed"
            },
            "Email hashed index must not be contained in the database"
        )
        assertFalse(
            indexes.any {
                it["name"] == EMAIL_UNIQUE_INDEX
                        && it["unique"] == true
                        && (it["key"] as Document).size == 1
                        && (it["key"] as Document)["email"] == 1
            },
            "Email unique index must not be contained in the database"
        )
    }

    companion object {
        private const val EMAIL_HASHED_INDEX = "email_hashed"
        private const val EMAIL_UNIQUE_INDEX = "email_1"
    }
}
