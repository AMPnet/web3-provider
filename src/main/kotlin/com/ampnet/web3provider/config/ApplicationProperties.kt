package com.ampnet.web3provider.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "com.ampnet.web3provider")
class ApplicationProperties {
    val provider = Provider()
}

class Provider {
    var blockchainApi = "https://eth-mainnet.alchemyapi.io/v2/hzYkWsp9ig3umpP6-ol3h2F64nof0lQ8"
}
