@file:JvmName("ContextUtils")

package com.kebab.core.util

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
internal class ContextUtilsInitializer(@Autowired context: ApplicationContext) {
    init {
        applicationContext = context
    }
}

internal lateinit var applicationContext: ApplicationContext

fun <T> getBean(clazz: Class<T>): T? = applicationContext.getBean(clazz)

inline fun <reified T> getBean() = getBean(T::class.java)

fun <T> getBean(beanName: String, clazz: Class<T>): T? = applicationContext.getBean(beanName, clazz)

inline fun <reified T> getBean(beanName: String) = getBean(beanName, T::class.java)
