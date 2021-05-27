package com.ampnet.web3provider.enums

@Suppress("MagicNumber")
enum class RedisEntity(val key: String, val ttlInSec: Long) {
    BALANCE("balance", 5),
    CODE("code", 5),
    POSITION("position", 5)
}
