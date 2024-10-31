plugins {
    id("delta-coverage-conventions")
    id("kotlin-conventions")
}

allprojects {
    group = "ua.marchenko"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

tasks.named("check") {
    dependsOn("detektMain", "detektTest")
}
