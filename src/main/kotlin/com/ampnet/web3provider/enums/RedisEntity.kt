package com.ampnet.web3provider.enums

enum class RedisEntity(val key: String, val ttlInSec: Long) {
    BALANCE("balance", 5)
}
