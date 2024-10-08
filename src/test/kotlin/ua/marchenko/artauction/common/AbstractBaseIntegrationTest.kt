package ua.marchenko.artauction.common

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.MongoDBContainer

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = [AbstractBaseIntegrationTest.TestContainerInitializer::class])
@EnableWebFluxSecurity
interface AbstractBaseIntegrationTest {

    companion object {
        val mongoDBContainer = MongoDBContainer("mongo:latest")
            .apply { start() }
    }

    class TestContainerInitializer: ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            mongoDBContainer.start()
            val testPropertyValues = TestPropertyValues.of(
                "spring.data.mongodb.uri=${mongoDBContainer.replicaSetUrl}"
            )
            testPropertyValues.applyTo(configurableApplicationContext.environment)
        }
    }
}
