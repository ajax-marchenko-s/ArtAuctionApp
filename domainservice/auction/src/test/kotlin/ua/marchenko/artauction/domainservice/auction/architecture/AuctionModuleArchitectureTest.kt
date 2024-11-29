package ua.marchenko.artauction.domainservice.auction.architecture

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.library.Architectures.onionArchitecture
import kotlin.test.Test
import ua.marchenko.artauction.domainservice.auction.infrastructure.common.mapper.CommonMapper
import ua.marchenko.artauction.domainservice.auction.domain.Auction


class AuctionModuleArchitectureTest {

    @Test
    fun `should follow valid onion architecture`() {
        onionArchitecture()
            .withOptionalLayers(true)
            .domainModels("..domain..")
            .applicationServices("..application..")
            .adapter("mongo", "..infrastructure.mongo..")
            .adapter("nats", "..infrastructure.nats..")
            .adapter("kafka", "..infrastructure.kafka..")
            .adapter("rest", "..infrastructure.rest..")
            .ignoreDependency(CommonMapper::class.java, Auction::class.java)
            .ignoreDependency(CommonMapper::class.java, Auction.Bid::class.java)
            .check(importedClasses)
    }

    companion object {
        private val importedClasses = ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .withImportOption(ImportOption.DoNotIncludeGradleTestFixtures())
            .importPackages("ua.marchenko.artauction.domainservice.auction")
    }
}
