package com.ampnet.web3provider.config

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImplExporter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JsonRpcConfig {

    @Bean
    fun jsonServiceExporter(): AutoJsonRpcServiceImplExporter = AutoJsonRpcServiceImplExporter()
}
