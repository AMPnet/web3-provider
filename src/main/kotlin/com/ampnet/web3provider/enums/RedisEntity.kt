package com.ampnet.web3provider.enums

@Suppress("MagicNumber")
enum class RedisEntity(val methodName: String, val ttlInSec: Long) {
    BALANCE("eth_getBalance", 5),
    CODE("eth_getCode", 5),
    CHAIN_ID("eth_chainId", 30 * 86400),
    TRANSACTION_BY_HASH("eth_getTransactionByHash", 1000)
}
