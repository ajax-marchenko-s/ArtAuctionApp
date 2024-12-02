package ua.marchenko.artauction.gateway.architecture

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.library.Architectures.onionArchitecture
import kotlin.test.Test

class GatewayModuleArchitectureTest {

    @Test
    fun `should follow valid onion architecture`() {
        onionArchitecture()
            .withOptionalLayers(true)
            .domainModels("..domain..")
            .applicationServices("..application..")
            .adapter("grpc", "..infrastructure.grpc..")
            .adapter("rest", "..infrastructure.rest..")
            .adapter("nats", "..infrastructure.nats..")
            .check(importedClasses)
    }

    companion object {
        private val importedClasses = ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .withImportOption(ImportOption.DoNotIncludeGradleTestFixtures())
            .importPackages("ua.marchenko.artauction.gateway")
    }

}
