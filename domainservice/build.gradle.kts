plugins {
    id("spring-conventions")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":internal-api"))
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.3.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.mongock:mongock-springboot-v3:5.4.4")
    implementation("io.mongock:mongodb-springdata-v4-driver:5.4.4")
    implementation("com.google.protobuf:protobuf-kotlin:4.28.2")
    implementation("io.nats:jnats:2.20.2")
    testImplementation("berlin.yuna:nats-server-embedded:2.10.21")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:testcontainers:1.19.0")
    testImplementation("org.testcontainers:mongodb:1.19.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.mockk:mockk:1.13.12")
}
