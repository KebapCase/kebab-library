package com.kebab.core.util

import org.apache.logging.log4j.LogManager

inline fun <reified T : Any> T.logger() = LogManager.getLogger(T::class.java)!!

inline fun <reified T : Any> T.lazyLogger() = lazy { logger() }

fun logger(name: String) = LogManager.getLogger(name)!!

fun lazyLogger(name: String) = lazy { LogManager.getLogger(name)!! }
