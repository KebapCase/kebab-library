package com.kebab.core.validation

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.annotation.AnnotationTarget.CONSTRUCTOR
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER
import kotlin.reflect.KClass

/**
 * Validates that email is valid
 *
 * @author Valentin Trusevich
 */
@Retention
@Target(PROPERTY_GETTER, FIELD, CONSTRUCTOR)
@MustBeDocumented
@Constraint(validatedBy = [EmailValidator::class, ListEmailValidator::class])
annotation class Email(val message: String = "provided email is invalid",
                       val groups: Array<KClass<*>> = [],
                       val payload: Array<KClass<out Payload>> = [])

/**
 * @author Valentin Trusevich
 */
class EmailValidator : ConstraintValidator<Email, String?> {

    override fun initialize(constraint: Email) {}

    override fun isValid(email: String?, context: ConstraintValidatorContext) =
            email?.valid() ?: true
}

/**
 * @author Valentin Trusevich
 *
 */
class ListEmailValidator : ConstraintValidator<Email, List<String>?> {

    override fun initialize(constraint: Email) {}

    override fun isValid(emails: List<String>?, context: ConstraintValidatorContext) =
            emails?.all { it.valid() } ?: true

}

private const val PATTERN = "(?=.{7,255}$)[a-zA-Z0-9_!#$%'*+\\-/=?^`{|}~.]{1,64}@[a-zA-Z0-9_!#\$%'*+\\-/=?^`{|}~]{2,253}\\.[a-zA-Z]{2,}$"

private fun String.valid() = Regex(PATTERN).matches(this)
