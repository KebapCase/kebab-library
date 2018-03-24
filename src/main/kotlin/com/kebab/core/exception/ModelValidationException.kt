package com.kebab.core.exception

import com.fasterxml.jackson.databind.util.ISO8601Utils.format
import com.kebab.core.exception.model.ErrorResource
import com.kebab.core.exception.model.ObjectErrorResource
import com.kebab.core.helper.ExceptionHandlingHelper.UTC_TIMEZONE
import com.kebab.core.util.toJson
import org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.Date

@ResponseStatus(value = UNPROCESSABLE_ENTITY, reason = ModelValidationException.REASON)
class ModelValidationException(private val errors: List<ObjectError>) : RuntimeException() {

    override val message: String
        get() = body().toJson()

    fun body(): ErrorResource {
        return ErrorResource(
                status = UNPROCESSABLE_ENTITY.value(),
                error = UNPROCESSABLE_ENTITY.reasonPhrase,
                message = REASON,
                fieldErrors = errors.map { ObjectErrorResource(it) },
                exception = ModelValidationException::class.java.canonicalName,
                timestamp = format(Date(), false, UTC_TIMEZONE)
        )
    }

    companion object {

        const val REASON = "The object provided contains validation errors, please check the object and fix all validation errors to proceed"

        private val serialVersionUID = 2937033552453543882L
    }
}
