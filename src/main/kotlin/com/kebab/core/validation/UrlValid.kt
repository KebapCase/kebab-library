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
 * Validates that url is valid and well formatted
 *
 * @author Valentin Trusevich
 */
@Retention
@Target(PROPERTY_GETTER, FIELD, CONSTRUCTOR)
@MustBeDocumented
@Constraint(validatedBy = [UrlValidValidator::class])
annotation class UrlValid(val message: String = "The URL is not properly formed. It must include 'http://' or 'https://' and only characters allowed in a web address.",
                          val groups: Array<KClass<*>> = [],
                          val payload: Array<KClass<out Payload>> = [])

/**
 * @author Valentin Trusevich
 */
class UrlValidValidator : ConstraintValidator<UrlValid, String?> {

    override fun initialize(constraint: UrlValid) {}

    override fun isValid(urlQuery: String?, context: ConstraintValidatorContext) =
            urlQuery.isNullOrBlank() || Regex(PATTERN).matches(urlQuery!!)

    companion object {

        private const val PATTERN = "(?=.{0,255}$)(https?|ftp|file)://\\w{2,}\\.[-a-zA-Z0-9+&@#/%?=~_|!:,.;]+"
    }
}
