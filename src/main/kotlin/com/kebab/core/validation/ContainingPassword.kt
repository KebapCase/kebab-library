package com.kebab.core.validation

import java.io.Serializable

/**
 * Should be implemented if password validation needed.
 * Works with [PasswordMatch] annotation
 *
 * @author Valentin Trusevich
 */
interface ContainingPassword : Serializable {

    var password: String

    var confirmPassword: String
}