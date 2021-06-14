package com.ampnet.web3provider.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "com.ampnet.web3provider")
class ApplicationProperties {
    val provider = Provider()
    val redis = RedisProperties()
}

class Provider {
    var blockchainApi = "https://eth-mainnet.alchemyapi.io/v2/hzYkWsp9ig3umpP6-ol3h2F64nof0lQ8"
}

@Suppress("MagicNumber")
class RedisProperties {
    var ethGetBalanceTtl: Long = 5
    var ethGetCodeTtl: Long = 5
    var ethChainIdTtl: Long = 30 * 86400
    var ethGetTransactionByHashTtl: Long = 10
}
