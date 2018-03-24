package com.kebab.core.exception

import com.kebab.core.exception.MalformedRequestDataException.Companion.REASON
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = BAD_REQUEST, reason = REASON)
class MalformedRequestDataException(message: String = REASON) : RuntimeException(message) {

    companion object {

        const val REASON = "Request contains no message or the message provided can not be processed or transformed to any known internal type"

        private const val serialVersionUID = 7723151391282511601L

    }

}
