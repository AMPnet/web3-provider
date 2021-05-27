package com.ampnet.web3provider.repository

import com.ampnet.web3provider.enums.RedisEntity

interface RedisRepository {
    fun getCache(key: String, hashKey: String): String?
    fun updateCache(redisEntity: RedisEntity, hashKey: String, value: String)
    fun deleteCache(key: String, hashKey: String)
}
