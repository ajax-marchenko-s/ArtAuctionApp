package ua.marchenko.artauction.gateway.infrastructure.rest.configuration.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass
import ua.marchenko.artauction.gateway.infrastructure.rest.dto.enums.ArtworkStyle

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Constraint(validatedBy = [StyleNotInSubsetValidator::class])
annotation class StyleNotInSubset(
    val excluded: Array<ArtworkStyle>,
    val message: String = "Value must not be in the excluded list: {excluded}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
