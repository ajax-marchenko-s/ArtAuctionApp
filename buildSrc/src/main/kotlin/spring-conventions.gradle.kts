plugins {
    id("kotlin-conventions")
    id("org.jetbrains.kotlin.plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    `java-test-fixtures`
}

dependencies {
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test:3.6.10") {
        exclude(module = "mockito-core")
    }
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
