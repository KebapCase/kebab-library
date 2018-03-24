package com.kebab.core.exception

import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = BAD_REQUEST, reason = IncorrectlyFormattedRequestException.REASON)
class IncorrectlyFormattedRequestException(message: String = REASON) : RuntimeException(message) {

    companion object {

        const val REASON = "Request doesn't contain all mandatory parameters or parameters provided are empty or formatted in an inappropriate way"

        private const val serialVersionUID = 6150722603321061364L

    }

}