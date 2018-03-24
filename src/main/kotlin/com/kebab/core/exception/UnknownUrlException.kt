package com.kebab.core.exception

import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = NOT_FOUND, reason = UnknownUrlException.REASON)
class UnknownUrlException(message: String = REASON) : RuntimeException(message) {

    companion object {

        const val REASON = "The endpoint for URL that you are trying to hit doesn't exist, try another URL to proceed"

        private const val serialVersionUID = -3276398150451602692L
    }

}
