package com.kebab.core.jms

import com.kebab.core.util.lazyLogger
import org.springframework.jms.core.JmsTemplate

class JmsMessageSenderImpl(private val jmsTemplate: JmsTemplate) : JmsMessageSender {

    val log by lazyLogger()

    override fun sendMessage(destinationName: String, message: String) {
        log.debug("Send message $message to $destinationName query")

        jmsTemplate.convertAndSend(destinationName, message)
    }

    override fun sendMessage(destinationName: String, message: Any) {
        log.debug("Send entity $message to $destinationName query")

        jmsTemplate.convertAndSend(destinationName, message)
    }
}