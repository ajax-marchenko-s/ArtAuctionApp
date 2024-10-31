package ua.marchenko.gateway.common.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import ua.marchenko.core.artwork.enums.ArtworkStyle

class StyleNotInSubsetValidator : ConstraintValidator<StyleNotInSubset, ArtworkStyle> {

    private lateinit var excludedValues: Set<ArtworkStyle>

    override fun initialize(constraint: StyleNotInSubset) {
        excludedValues = constraint.excluded.toSet()
    }

    override fun isValid(value: ArtworkStyle, context: ConstraintValidatorContext) = !excludedValues.contains(value)
}
