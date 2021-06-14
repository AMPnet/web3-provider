package com.ampnet.web3provider.repository

import com.ampnet.web3provider.enums.RedisEntity
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class RedisRepositoryImpl(
    private val redisTemplate: RedisTemplate<String, Any>
) : RedisRepository {

    private val hashOperations: HashOperations<String, String, Any> = redisTemplate.opsForHash()

    override fun getCache(key: String, hashKey: String?): Any? {
        return hashKey?.let { hashOperations.get(key, hashKey) }
    }

    override fun updateCache(redisEntity: RedisEntity, hashKey: String?, value: Any) {
        hashKey?.let {
            hashOperations.put(redisEntity.methodName, hashKey, value)
            redisTemplate.expire(redisEntity.methodName, Duration.ofSeconds(redisEntity.ttlInSec))
        }
    }

    override fun deleteCache(key: String, hashKey: String) {
        hashOperations.delete(key, hashKey)
    }
}
