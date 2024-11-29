plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "artauction"
include("common-proto", "core", "domainservice", "gateway", "grpc-api", "internal-api")
include("domainservice:artwork")
include("domainservice:auction")
include("domainservice:common")
include("domainservice:migration")
include("domainservice:user")
