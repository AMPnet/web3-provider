package com.ampnet.web3provider.enums

import com.ampnet.web3provider.config.ApplicationProperties

sealed class RedisEntity(val methodName: String, val ttlInSec: Long) {
    class Balance(applicationProperties: ApplicationProperties) :
        RedisEntity("eth_getBalance", applicationProperties.redis.ethGetBalanceTtl)
    class Code(applicationProperties: ApplicationProperties) :
        RedisEntity("eth_getCode", applicationProperties.redis.ethGetCodeTtl)
    class ChainId(applicationProperties: ApplicationProperties) :
        RedisEntity("eth_chainId", applicationProperties.redis.ethChainIdTtl)
    class TransactionByHash(applicationProperties: ApplicationProperties) :
        RedisEntity("eth_getTransactionByHash", applicationProperties.redis.ethGetTransactionByHashTtl)
}
