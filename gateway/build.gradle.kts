plugins {
    id("spring-conventions")
}

dependencies {
    implementation(project(":internal-api"))
    implementation(project(":core"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.2")
    implementation("io.nats:jnats:2.16.14")
    implementation("com.google.protobuf:protobuf-kotlin:4.28.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation(kotlin("test"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
