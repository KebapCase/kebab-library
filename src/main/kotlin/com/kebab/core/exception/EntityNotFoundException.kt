package com.kebab.core.exception

import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = NOT_FOUND, reason = EntityNotFoundException.REASON)
class EntityNotFoundException(message: String = REASON) : RuntimeException(message) {

    companion object {

        const val REASON = "There were no data found by parameters provided"

        private const val serialVersionUID = -3252849816876657956L

    }

}
