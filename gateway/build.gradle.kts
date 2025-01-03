plugins {
    id("spring-conventions")
    id("grpc-conventions")
}

dependencies {
    implementation(project(":internal-api"))
    implementation(project(":core"))
    implementation(project(":grpc-api"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.2")
    implementation("com.google.protobuf:protobuf-kotlin:4.28.2")
    implementation("net.devh:grpc-server-spring-boot-starter:3.1.0.RELEASE")
    implementation("net.devh:grpc-spring-boot-starter:3.1.0.RELEASE")
    implementation("systems.ajax:nats-spring-boot-starter:4.1.0.186.MASTER-SNAPSHOT")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("com.tngtech.archunit:archunit-junit5:1.3.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
