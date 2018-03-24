package com.kebab.core.exception

import com.kebab.core.exception.UnauthorizedException.Companion.REASON
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * The exception highlights that requested link out does not belong to specified account or does not exist
 *
 * @author Valentin Trusevich
 */
@ResponseStatus(value = UNAUTHORIZED, reason = REASON)
class UnauthorizedException(message: String = REASON) : RuntimeException(message) {
    companion object {

        const val REASON = "Requested record does not belong to specified account or does not exist"

        private const val serialVersionUID = -2343014087618774676L
    }
}