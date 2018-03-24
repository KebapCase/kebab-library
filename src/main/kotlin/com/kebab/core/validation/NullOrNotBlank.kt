package com.kebab.core.validation

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER
import kotlin.reflect.KClass

@Target(PROPERTY_GETTER, FIELD)
@Retention
@MustBeDocumented
@Constraint(validatedBy = [NullOrNotBlankValidator::class, NullOrNotEmptyValidator::class])
annotation class NullOrNotBlank(val message: String = "Object should be either null or not blank (empty)",
                                val groups: Array<KClass<*>> = [],
                                val payload: Array<KClass<out Payload>> = [])

/**
 * @author Alexander Yugov
 */
class NullOrNotBlankValidator : ConstraintValidator<NullOrNotBlank, String?> {

    override fun initialize(constraint: NullOrNotBlank) {}

    override fun isValid(value: String?, context: ConstraintValidatorContext) =
            value == null || value.isNotBlank()

}

class NullOrNotEmptyValidator : ConstraintValidator<NullOrNotBlank, Collection<Any>?> {

    override fun initialize(constraint: NullOrNotBlank) {}

    override fun isValid(value: Collection<Any>?, context: ConstraintValidatorContext) =
            value == null || value.isNotEmpty()

}