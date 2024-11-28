plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "artauction"
include("common-proto", "core", "domainservice", "gateway", "grpc-api", "internal-api")
include("domainservice:artwork")
findProject(":domainservice:artwork")?.name = "artwork"
include("domainservice:user")
findProject(":domainservice:user")?.name = "user"
include("domainservice:common")
findProject(":domainservice:common")?.name = "common"
include("domainservice:auction")
findProject(":domainservice:auction")?.name = "auction"
include("domainservice:migration")
findProject(":domainservice:migration")?.name = "migration"
