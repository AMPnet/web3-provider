package com.ampnet.web3provider.repository

import com.ampnet.web3provider.enums.RedisEntity

interface RedisRepository {
    fun getCache(key: String, hashKey: String?): Any?
    fun updateCache(redisEntity: RedisEntity, hashKey: String?, value: Any)
    fun deleteCache(key: String, hashKey: String)
}
