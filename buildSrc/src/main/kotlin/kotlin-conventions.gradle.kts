plugins {
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

detekt {
    buildUponDefaultConfig = true
    config.from(file("${rootDir}/config/detekt.yaml"))
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging { showStandardStreams = true }
    systemProperty("junit.jupiter.extensions.autodetection.enabled", "true")
}
