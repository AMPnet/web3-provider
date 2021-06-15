package com.ampnet.web3provider.enums

enum class RedisEntity(val methodName: String) {
    BALANCE("eth_getBalance"),
    CODE("eth_getCode"),
    CHAIN_ID("eth_chainId"),
    TRANSACTION_BY_HASH("eth_getTransactionByHash")
}
