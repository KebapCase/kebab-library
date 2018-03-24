@file:JvmName("ValidationUtils")

package com.kebab.core.util

import com.kebab.core.exception.ApplicationInitializationException
import com.kebab.core.exception.MalformedRequestDataException
import com.kebab.core.exception.ModelValidationException
import org.apache.commons.lang3.StringUtils.EMPTY
import org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase
import org.springframework.beans.BeanUtils.isSimpleProperty
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.ObjectError
import org.springframework.validation.Validator
import org.springframework.web.multipart.MultipartFile
import java.io.Serializable
import javax.validation.ConstraintValidatorContext

private val validator by lazy {
    getBean<Validator>("mvcValidator")
            ?: throw ApplicationInitializationException("System was not able to find appropriate bean for validation needs")
}

fun <T : Any> T?.validate(): T {

    if (this == null) throw MalformedRequestDataException()

    with(validateObject(this)) { if (isNotEmpty()) throw ModelValidationException(this) }

    return this
}

fun <T : Collection<Any?>> T?.validate(): T {

    if (this == null || isEmpty()) throw MalformedRequestDataException()

    with(validateCollection(this)) { if (isNotEmpty()) throw ModelValidationException(this) }

    return this
}

/**
 * Method validates collection provided and throws a new [ModelValidationException] in case of any item is not valid
 *
 * @param collection Collection of items to check.
 * @since 1.0.0
 */
fun validate(collection: Collection<Any?>) {
    if (collection.isEmpty()) {
        throw MalformedRequestDataException()
    }

    with(validateCollection(collection, mutableListOf(), EMPTY)) {
        if (isNotEmpty()) {
            throw ModelValidationException(this)
        }
    }
}

/**
 * Method validates entity provided and throws a new [ModelValidationException] in case of any fields's
 * validation error.
 *
 * @param entity Model that contains constraint annotations.
 * @since 1.0.0
 */
fun validate(entity: Any?) {
    if (entity == null) {
        throw MalformedRequestDataException()
    }

    with(validateObject(entity, mutableListOf(), EMPTY, null)) {
        if (isNotEmpty()) {
            throw ModelValidationException(this)
        }
    }
}

fun <T : Serializable> T.validate(): T {
    validate(this)

    return this
}

private fun validateObject(data: Any, validated: MutableList<Any> = mutableListOf(), parent: String = EMPTY, index: Int? = null): List<ObjectError> {

    if (validated.any { it === data }) {
        return emptyList()
    }

    validated.add(data)

    val resourceName = resourceName(data, parent, index)

    val bindingResult = BeanPropertyBindingResult(data, resourceName)

    validator.validate(data, bindingResult)

    val objectErrors =
            fields(data.javaClass)
                    .filter { !isSimpleProperty(it.type) }
                    .mapNotNull { readValue(data, it) }
                    .flatMap {
                        if (Collection::class.java.isAssignableFrom(it::class.java)) {
                            validateCollection(it as Collection<Any?>, validated, resourceName)
                        } else {
                            validateObject(it, validated, resourceName)
                        }
                    }

    return listOf(bindingResult.fieldErrors, objectErrors, bindingResult.globalErrors).flatMap { it }
}

private fun validateCollection(data: Collection<Any?>?, validated: MutableList<Any> = mutableListOf(), parent: String = EMPTY): List<ObjectError> {
    data ?: return emptyList()

    val list = data.filterNotNull()

    return (0 until list.size).toList().flatMap { validateObject(list[it], validated, parent, it) }
}

private fun resourceName(data: Any, parent: String, index: Int?): String {
    val builder = StringBuilder(parent.trim().decapitalize())

    if (builder.isNotEmpty()) {
        builder.append('.')
    }

    builder.append(data.javaClass.simpleName.decapitalize())

    if (index != null) {
        builder.append("[$index]")
    }

    return builder.toString()
}

fun MultipartFile.validate(allowableFormats: Collection<String>, maxSize: Int): MultipartFile {
    val errors = mutableListOf<ObjectError>()

    if (!equalsAnyIgnoreCase(originalFilename.substringAfterLast("."), *(allowableFormats.toTypedArray())))
        errors.add(ObjectError("format", "File format must be in: $allowableFormats"))
    if (size > maxSize.toBytes())
        errors.add(ObjectError("size", "File size must be less than: $maxSize MB"))

    if (errors.isNotEmpty()) throw ModelValidationException(errors)

    return this
}

fun Int.toBytes() = toLong() * 1024 * 1024

fun ConstraintValidatorContext.addCustomField(field: String, message: String) = apply {
    disableDefaultConstraintViolation()
    buildConstraintViolationWithTemplate(message).addPropertyNode(field).addConstraintViolation()
}