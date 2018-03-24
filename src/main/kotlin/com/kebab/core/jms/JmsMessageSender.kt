package com.kebab.core.jms

import org.springframework.scheduling.annotation.Async

@Async
interface JmsMessageSender {

    fun sendMessage(destinationName: String, message: String)

    fun sendMessage(destinationName: String, message: Any)
}
