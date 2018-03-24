package com.kebab.core.exception.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY
import org.apache.commons.lang3.StringUtils.EMPTY
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import java.io.Serializable

@JsonInclude(NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
data class ObjectErrorResource(

        val resource: String = EMPTY,

        val field: String = EMPTY,

        val code: String = EMPTY,

        val message: String = EMPTY

) : Serializable {
    constructor(error: ObjectError) : this(
            error.objectName ?: EMPTY,
            (error as? FieldError)?.field ?: EMPTY,
            error.code ?: EMPTY,
            error.defaultMessage ?: EMPTY)

    companion object {

        private const val serialVersionUID = 2937033552453543882L

    }
}
