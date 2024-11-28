plugins {
    id("subproject-conventions")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":internal-api"))
    implementation(project(":domainservice:user"))
    implementation(project(":domainservice:common"))
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("systems.ajax:nats-spring-boot-starter:4.1.0.186.MASTER-SNAPSHOT")
    implementation("com.google.protobuf:protobuf-kotlin:3.25.5")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive:3.3.5")
    testImplementation(kotlin("test"))
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:testcontainers:1.19.0")
    testImplementation("org.testcontainers:mongodb:1.19.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.mockk:mockk:1.13.12")
    testFixturesImplementation(testFixtures(project(":domainservice:user")))
    testImplementation(testFixtures(project(":domainservice:user")))
}
