package com.kebab.core.exception

import org.apache.commons.lang3.StringUtils.defaultString
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = INTERNAL_SERVER_ERROR, reason = ApplicationInitializationException.REASON)
class ApplicationInitializationException : RuntimeException {

    constructor(message: String) : super(message)

    constructor(cause: Throwable) : super(cause)

    override val message: String?
        get() = defaultString(super.message, REASON)

    companion object {

        const val REASON = "System was not initialized properly during bootstrap and startup phases"

        private val serialVersionUID = 2937033552453543882L

    }

}
