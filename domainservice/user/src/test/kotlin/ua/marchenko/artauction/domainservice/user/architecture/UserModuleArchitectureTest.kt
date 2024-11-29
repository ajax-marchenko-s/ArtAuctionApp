package ua.marchenko.artauction.domainservice.user.architecture

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.library.Architectures.onionArchitecture
import kotlin.test.Test

class UserModuleArchitectureTest {

    @Test
    fun `should follow valid onion architecture`() {
        onionArchitecture()
            .withOptionalLayers(true)
            .domainModels("..domain..")
            .applicationServices("..application..")
            .adapter("mongo", "..infrastructure.mongo..")
            .adapter("rest", "..infrastructure.rest..")
            .adapter("proto", "..infrastructure.proto..")
            .check(importedClasses)
    }

    companion object {
        private val importedClasses = ClassFileImporter()
            .importPackages("ua.marchenko.artauction.domainservice.auction")
    }
}
