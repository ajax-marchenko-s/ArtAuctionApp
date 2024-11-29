package ua.marchenko.artauction.domainservice.user.architecture

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.library.Architectures.onionArchitecture
import kotlin.test.Test

class ArtworkModuleArchitectureTest {

    @Test
    fun `should follow valid onion architecture`() {
        onionArchitecture()
            .withOptionalLayers(true)
            .domainModels("..domain..")
            .applicationServices("..application..")
            .adapter("mongo", "..infrastructure.mongo..")
            .adapter("nats", "..infrastructure.nats..")
            .adapter("redis", "..infrastructure.redis..")
            .adapter("rest", "..infrastructure.rest..")
            .check(importedClasses)
    }

    companion object {
        private val importedClasses = ClassFileImporter()
            .importPackages("ua.marchenko.artauction.domainservice.artwork")
    }
}
