package com.kebab.core.config.aws.jms

import com.kebab.core.jms.JmsMessageSender
import com.kebab.core.jms.JmsMessageSenderImpl
import com.rabbitmq.jms.admin.RMQConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.config.DefaultJmsListenerContainerFactory
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.support.converter.MappingJackson2MessageConverter
import org.springframework.jms.support.converter.MessageConverter
import org.springframework.jms.support.converter.MessageType.TEXT
import org.springframework.jms.support.destination.DynamicDestinationResolver
import javax.jms.ConnectionFactory
import javax.jms.Session.CLIENT_ACKNOWLEDGE

@Configuration
class JmsConfig {

    @Bean
    fun jmsMessageSender(connectionFactory: ConnectionFactory): JmsMessageSender =
            JmsMessageSenderImpl(JmsTemplate(connectionFactory))

    @EnableJms
    @Configuration
    class JmsCloudConfig {

        @Bean
        fun connectionFactory(): ConnectionFactory = RMQConnectionFactory().apply {
            port = 5672
            host = "kebab-rabbitmq"
            username = "kebab"
            password = "kebab"
        }

        @Bean
        fun jmsListenerContainerFactory(connectionFactory: ConnectionFactory) =
                DefaultJmsListenerContainerFactory().apply {
                    setConnectionFactory(connectionFactory)
                    setDestinationResolver(DynamicDestinationResolver())
                    setConcurrency(CONCURRENCY)
                    setSessionAcknowledgeMode(CLIENT_ACKNOWLEDGE)
                }

        @Bean
        fun messageConverter(): MessageConverter {
            val converter = MappingJackson2MessageConverter()
            converter.setTargetType(TEXT)
            converter.setTypeIdPropertyName("_type")
            return converter
        }

        companion object {

            private const val CONCURRENCY = "3-10"
        }
    }
}

