package com.kebab.core.validation

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FILE
import kotlin.reflect.KClass

/**
 * Validates that fields password and confirmPassword have the same values.
 * Validated class should implement [ContainingPassword] interface
 *
 * @author Valentin Trusevich
 */
@Retention
@Target(CLASS, FILE)
@MustBeDocumented
@Constraint(validatedBy = [PasswordMatchValidator::class])
annotation class PasswordMatch(val message: String = "passwords should match",
                               val groups: Array<KClass<*>> = [],
                               val payload: Array<KClass<out Payload>> = [])

/**
 * @author Valentin Trusevich
 */
class PasswordMatchValidator : ConstraintValidator<PasswordMatch, ContainingPassword> {

    override fun initialize(constraint: PasswordMatch) {}

    override fun isValid(obj: ContainingPassword, context: ConstraintValidatorContext) =
            obj.password == obj.confirmPassword
}
