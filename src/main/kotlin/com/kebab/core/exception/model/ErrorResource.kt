package com.kebab.core.exception.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY
import java.io.Serializable

@JsonInclude(NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
data class ErrorResource(

        var timestamp: String? = null,

        var status: Int = 0,

        var error: String? = null,

        var message: String? = null,

        var exception: String? = null,

        var fieldErrors: List<ObjectErrorResource> = mutableListOf()

) : Serializable {

    companion object {

        private const val serialVersionUID = -733017142423808234L
    }
}