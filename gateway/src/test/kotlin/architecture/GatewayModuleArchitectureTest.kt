package architecture

import com.tngtech.archunit.core.importer.ClassFileImporter
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
            .importPackages("ua.marchenko.artauction.gateway")
    }

}
