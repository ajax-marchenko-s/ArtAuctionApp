package ua.marchenko.artauction.gateway.infrastructure.rest.configuration.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import ua.marchenko.artauction.gateway.infrastructure.rest.dto.enums.ArtworkStyle

class StyleNotInSubsetValidator : ConstraintValidator<StyleNotInSubset, ArtworkStyle> {

    private lateinit var excludedValues: Set<ArtworkStyle>

    override fun initialize(constraint: StyleNotInSubset) {
        excludedValues = constraint.excluded.toSet()
    }

    override fun isValid(value: ArtworkStyle, context: ConstraintValidatorContext) = !excludedValues.contains(value)
}
