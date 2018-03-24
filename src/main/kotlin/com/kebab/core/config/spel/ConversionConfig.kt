package com.kebab.core.config.spel

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.support.DefaultConversionService

/**
 * @author Valentin Trusevich
 */
@Configuration
class ConversionConfig {

    @Bean
    fun conversionService() = DefaultConversionService()

}