@file:JvmName("JsonUtils")

package com.kebab.core.util

import com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_MISSING_VALUES
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT
import com.kebab.core.paging.model.OrderBy
import org.apache.commons.lang3.StringUtils.EMPTY
import org.springframework.data.domain.Sort
import java.io.IOException

private val log by lazyLogger("JsonUtils")

const val EMPTY_TEMPLATE = "{ }"

private val _mapper = ObjectMapper().apply {
    configure(ALLOW_MISSING_VALUES, true)
    enable(INDENT_OUTPUT)
}

fun mapper() = _mapper

@JvmOverloads
inline fun <reified T> String.fromJson(mapper: ObjectMapper = mapper()) = toObject(this, T::class.java, mapper)

@JvmOverloads
fun Any?.toJson(mapper: ObjectMapper = mapper()) =
        this?.let {
            try {
                mapper.writeValueAsString(it)
            } catch (exception: JsonProcessingException) {
                log.error("Unexpected error occurred during conversion of  to JSON representation", it.javaClass.canonicalName, exception)
                EMPTY
            }

        } ?: EMPTY

fun Iterable<Sort.Order>.toJson() = with(this.mapNotNull { OrderBy.parse(it) }) {
    when {
        isEmpty() -> EMPTY_TEMPLATE
        else -> toJson()
    }
}

@JvmOverloads
fun <T> toObject(json: String, clazz: Class<T>, mapper: ObjectMapper = _mapper): T {
    val normalizedJson = json.trim()

    if (normalizedJson.isEmpty()) {
        throw com.kebab.core.exception.MalformedRequestDataException()
    }

    try {
        return mapper.readValue(normalizedJson, clazz)
    } catch (exception: IOException) {
        log.error("Unexpected error occurred during deserialization from JSON to Object", exception)

        throw com.kebab.core.exception.MalformedRequestDataException()
    }
}
