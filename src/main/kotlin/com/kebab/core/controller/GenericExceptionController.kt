package com.kebab.core.controller

import com.kebab.core.exception.UnknownUrlException
import com.kebab.core.helper.ExceptionHandlingHelper.EXCEPTION_ATTRIBUTE
import com.kebab.core.helper.ExceptionHandlingHelper.MESSAGE_ATTRIBUTE
import com.kebab.core.helper.ExceptionHandlingHelper.STATUS_ATTRIBUTE
import com.kebab.core.util.lazyLogger
import com.kebab.core.util.toJson
import io.swagger.annotations.Api
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes
import org.springframework.boot.autoconfigure.web.ErrorController
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.valueOf
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.ServletRequestAttributes
import javax.servlet.http.HttpServletRequest

@RestController
@Api(hidden = true)
class GenericExceptionController(private val defaultErrorAttributes: DefaultErrorAttributes) : ErrorController {

    private val log by lazyLogger()

    @RequestMapping(PATH, produces = [APPLICATION_JSON_UTF8_VALUE])
    fun error(request: HttpServletRequest): ResponseEntity<Map<String, Any>> {
        val requestAttributes = ServletRequestAttributes(request)

        val attributes = defaultErrorAttributes.getErrorAttributes(requestAttributes, false)

        val message = (attributes[MESSAGE_ATTRIBUTE] as String).trim()

        val httpStatus = valueOf(attributes[STATUS_ATTRIBUTE] as Int)

        val exception =
                if (httpStatus == NOT_FOUND && attributes[EXCEPTION_ATTRIBUTE] == null) {
                    if (DEFAULT_MESSAGE == message) {
                        attributes[MESSAGE_ATTRIBUTE] = UnknownUrlException.REASON
                    }

                    UnknownUrlException(UnknownUrlException.REASON)
                } else {
                    defaultErrorAttributes.getError(requestAttributes)
                }

        log.error(attributes.toJson(), exception)

        return status(httpStatus).contentType(APPLICATION_JSON_UTF8).body(attributes)
    }

    override fun getErrorPath() = PATH

    companion object {

        private const val DEFAULT_MESSAGE = "No message available"

        private const val PATH = "/error"
    }
}