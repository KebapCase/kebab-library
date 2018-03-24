package com.kebab.core.helper

import com.fasterxml.jackson.databind.util.ISO8601Utils.format
import com.kebab.core.util.lazyLogger
import com.kebab.core.util.toJson
import org.apache.commons.lang3.ArrayUtils.isNotEmpty
import org.apache.commons.lang3.StringUtils.trimToNull
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import java.lang.System.lineSeparator
import java.util.Date
import java.util.TimeZone

object ExceptionHandlingHelper {

    val UTC_TIMEZONE = TimeZone.getTimeZone("UTC")!!

    const val MESSAGE_ATTRIBUTE = "message"

    const val STATUS_ATTRIBUTE = "status"

    const val EXCEPTION_ATTRIBUTE = "exception"

    private val log by lazyLogger()

    private const val TIMESTAMP_ATTRIBUTE = "timestamp"

    private const val ERROR_ATTRIBUTE = "error"

    fun toResponse(exception: Exception, httpStatus: HttpStatus, vararg messagesArray: String?): ResponseEntity<String> {
        val attributes = mutableMapOf(
                TIMESTAMP_ATTRIBUTE to format(Date(), false, UTC_TIMEZONE),
                STATUS_ATTRIBUTE to httpStatus.value(),
                ERROR_ATTRIBUTE to httpStatus.reasonPhrase,
                EXCEPTION_ATTRIBUTE to exception.javaClass.canonicalName)

        if (isNotEmpty(messagesArray)) {
            val message = messagesArray.mapNotNull { trimToNull(it) }.joinToString(separator = lineSeparator())

            if (message.isNotBlank()) {
                attributes[MESSAGE_ATTRIBUTE] = message
            }
        } else {
            attributes[MESSAGE_ATTRIBUTE] = exception.message
        }

        return with(attributes.toJson()) {
            log.error(this, exception)

            status(httpStatus).contentType(APPLICATION_JSON_UTF8).body(this)
        }
    }
}
