package com.kebab.core

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@ComponentScan("com.kebab.core")
@EnableTransactionManagement
class Configurator
