@file:JvmName("ObjectUtils")

package com.kebab.core.util

import com.fasterxml.jackson.annotation.JsonIgnore
import org.apache.commons.lang3.StringUtils.isAlphanumeric
import org.apache.commons.lang3.reflect.FieldUtils.getAllFields
import org.apache.commons.lang3.reflect.FieldUtils.getFieldsListWithAnnotation
import org.apache.logging.log4j.LogManager.getLogger
import org.springframework.beans.BeanUtils.copyProperties
import org.springframework.beans.BeanWrapperImpl
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import java.io.Serializable
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.MalformedParametersException
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.lang.reflect.Modifier.isFinal
import java.lang.reflect.Modifier.isStatic
import javax.persistence.ElementCollection
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.persistence.Transient

private val log by lazy { getLogger("ObjectUtils")!! }

private val ignoredFieldAnnotations = listOf(
        JsonIgnore::class.java,
        LastModifiedDate::class.java,
        CreatedDate::class.java,
        Transient::class.java
)

private val associationAnnotations = listOf(
        OneToOne::class.java,
        OneToMany::class.java,
        ManyToOne::class.java,
        ManyToMany::class.java,
        ElementCollection::class.java
)

private val syntheticAnnotations = listOf(
        Id::class.java,
        GeneratedValue::class.java,
        CreatedDate::class.java,
        CreatedBy::class.java,
        LastModifiedDate::class.java,
        LastModifiedBy::class.java
)

private val readMethods = mutableMapOf<Class<*>, Map<String, Method>>()

private val writeMethods = mutableMapOf<Class<*>, Map<String, Method>>()

private val classFields = mutableMapOf<Class<*>, Map<String, Field>>()

private val ignoredFields = mutableMapOf<Class<*>, Set<String>>()

private val associationFields = mutableMapOf<Class<*>, Set<String>>()

/**
 * Returns available read method from the class-differed cache.
 *
 * @param clazz     Type of object to look read method definitions by.
 * @param fieldName Name of the read field.
 * @return Existing read [Method] in case if exist, otherwise a new instance of the same [Method]. In
 * case if the fieldName starts with {@value GENERATED_FIELD_PREFIX} method returns `null`.
 * @since 1.0.0
 */
fun readMethod(clazz: Class<*>, fieldName: String): Method? {
    if (fieldName.startsWith("$")) {
        return null
    }

    readMethods[clazz] ?: registerReadMethodsMap(clazz)

    return readMethods[clazz]?.get(fieldName)
}

/**
 *
 * *Null Safe* Returns value read from the [Method] with `fieldName` provided.
 *
 * @param fieldName Name of the read field.
 * @param target    The target object to read `value` from.
 * @param params    Optional parameters of the target method.
 * @return `value` of the field if it exists, otherwise returns `null`.
 * @since 1.0.0
 */
fun readValue(target: Any?, fieldName: String, vararg params: Any): Any? {
    target ?: return null

    val clazz = target.javaClass

    val method = readMethod(clazz, fieldName) ?: return null

    return try {
        method.invoke(target, *params)
    } catch (exception: IllegalAccessException) {
        log.error("Unexpected error occurred during reading a value from the method <{}> of {}",
                fieldName, target.javaClass.simpleName, exception)

        null
    } catch (exception: InvocationTargetException) {
        log.error("Unexpected error occurred during reading a value from the method <{}> of {}", fieldName, target.javaClass.simpleName, exception)

        null
    }
}

fun Serializable.writeTo(fieldName: String, value: Any?): Any? {
    return writeValue(this, fieldName, value)
}

/**
 *
 * *Null Safe* Returns value read from the [read-method][Method] of `field` provided.
 *
 * @param field  Target [Field] to read from.
 * @param target The target object to read `value` from.
 * @param params Optional parameters of the target method.
 * @return `value` of the field if it exists, otherwise returns `null`.
 * @since 1.0.0
 */
fun readValue(target: Any, field: Member, vararg params: Any) = readValue(target, field.name, *params)

fun <T : Serializable> T.mergeWith(target: T): T {
    mergeEntities(this, target)

    return target
}

fun mergeEntities(source: Serializable, target: Serializable) {
    val clazz = source.javaClass

    val ignoredProperties = listOf(
            nullPropertyNames(source),
            ignoredFields[clazz].orEmpty(),
            collectionNames(clazz))
            .flatMap { it }
            .distinct()

    copyProperties(source, target, *ignoredProperties.toTypedArray())

    mergeInnerCollections(source, target)
}

/**
 * Returns available write method from the class-differed cache.
 *
 * @param clazz     Type of object to look write method definitions by.
 * @param fieldName Name of the field.
 * @return Existing write [Method] in case if exist, otherwise a new instance of the same [Method]. In
 * case if the fieldName starts with {@value GENERATED_FIELD_PREFIX} method returns `null`.
 * @since 1.0.0
 */
fun writeMethod(clazz: Class<*>, fieldName: String): Method? {
    if (fieldName.startsWith("$")) {
        return null
    }

    writeMethods[clazz] ?: registerWriteMethodsMap(clazz)

    return writeMethods[clazz]?.get(fieldName)
}

/**
 * Method writes the **value** provided to the specific `write method` of **field**
 * of the **target** object's instance defined.
 *
 *In case of exceptions ([ ],[InvocationTargetException]) method rethrows [ ].
 *
 * @param target Object to write `value` to
 * @param field  The name of field to write to
 * @param value  The value to write
 * @throws MalformedParametersException The wrapper for Reflection-specific exception that may occur during
 * invocation
 * @see Method.invoke
 * @see .writeMethod
 * @since 1.0.0
 */
fun writeValue(target: Any, field: String, value: Any?) {
    val method = writeMethod(target.javaClass, field) ?: return

    try {
        method.invoke(target, value)
    } catch (exception: IllegalAccessException) {
        log.error("Unexpected error occurred during work with {} field", field, exception)

        throw MalformedParametersException(exception.message)
    } catch (exception: InvocationTargetException) {
        log.error("Unexpected error occurred during work with {} field", field, exception)
        throw MalformedParametersException(exception.message)
    }

}

fun field(clazz: Class<*>, fieldName: String) = cachedClassFields(clazz)[fieldName]

/**
 * Method scans local cache for available [fields][Field] of the specified [class type][Class] provided.
 * In case if local cache has nothing to return, method [analyzes the target class][.registerUsefulClassFields].
 *
 * @param clazz Class type to retrieve declared fields by
 * @return [Map] of [field name][Field.getName] -> [Field] pairs.
 * @see .registerUsefulClassFields
 * @see Class.getDeclaredFields
 * @see Class.getFields
 * @see Field
 *
 * @since 1.0.0
 */
private fun cachedClassFields(clazz: Class<*>) = classFields[clazz] ?: registerClassFields(clazz)

fun fields(clazz: Class<*>) = cachedClassFields(clazz).values

private fun registerClassFields(clazz: Class<*>): Map<String, Field> = synchronized(classFields) {
    val ignored = ignoredFieldAnnotations
            .flatMap { getFieldsListWithAnnotation(clazz, it) }
            .mapNotNull { it?.name }
            .toSet()

    val synthetic = syntheticAnnotations
            .flatMap { getFieldsListWithAnnotation(clazz, it) }
            .mapNotNull { it?.name }

    val usefulFields = getAllFields(clazz)
            .filterNotNull()
            .filterNot { ignored.contains(it.name) }
            .filterNot { isStatic(it.modifiers) }
            .filterNot { isFinal(it.modifiers) }
            .filter { isAlphanumeric(it.name) }
            .map { it.name!! to it }
            .toMap()

    val associations = associationAnnotations
            .flatMap { getFieldsListWithAnnotation(clazz, it) }
            .mapNotNull { it?.name }
            .filter { usefulFields[it] != null }
            .filterNot { ignored.contains(it) }
            .toSet()

    classFields[clazz] = usefulFields

    associationFields[clazz] = associations
    ignoredFields[clazz] = ignored.plus(synthetic).distinct().toSet()

    return usefulFields
}

private fun registerReadMethodsMap(clazz: Class<*>) = synchronized(readMethods) {
    if (readMethods[clazz] != null) {
        return
    }

    readMethods[clazz] = BeanWrapperImpl(clazz).propertyDescriptors
            .map { it to field(clazz, it.name) }
            .toMap()
            .filter { it.value != null }
            .keys
            .filter { it.readMethod != null }
            .map { it.name to it.readMethod }
            .toMap()
}

private fun registerWriteMethodsMap(clazz: Class<*>) = synchronized(writeMethods) {
    if (writeMethods[clazz] != null) {
        return
    }

    writeMethods[clazz] = BeanWrapperImpl(clazz).propertyDescriptors
            .map { it to field(clazz, it.name) }
            .toMap()
            .filter { it.value != null }
            .keys
            .filter { it.writeMethod != null }
            .map { it.name to it.writeMethod }
            .toMap()
}

/**
 * Method scans the object provided and returns names of all **null-value** fields.
 *
 * @param target Target class to scan
 * @return [Collection][Set] of names
 * @see .fields
 * @since 1.0.0
 */
fun nullPropertyNames(target: Any) =
        fields(target.javaClass)
                .filter { readValue(target, it) == null }
                .mapNotNull { it.name }
                .toSet()

/**
 * Method scans the class provided and returns names of all collection-like fields.
 *
 * @param target Target class to scan
 * @return [Collection][Set] of names
 */
fun collectionNames(target: Class<*>) =
        fields(target)
                .filter { Collection::class.java.isAssignableFrom(it.type) }
                .mapNotNull { it.name }
                .toSet()

fun mergeInnerCollections(source: Serializable, target: Serializable) {
    collectionNames(target.javaClass).map {
        mergeCollections(it, source, target)
    }
}

private fun mergeCollections(collectionName: String,
                             source: Serializable,
                             target: Serializable) {
    val sourceCollection = readValue(source, collectionName) as Collection<Any?>?

    if (sourceCollection?.isEmpty() != false) {
        return
    }

    @Suppress("UNCHECKED_CAST")
    val targetCollection = readValue(target, collectionName) as MutableCollection<Any?>?

    targetCollection ?: return

    targetCollection.clear()

    targetCollection.addAll(sourceCollection)


    target.writeTo(collectionName, targetCollection)
}