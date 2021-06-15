package com.ampnet.web3provider.repository

import com.ampnet.web3provider.config.ApplicationProperties
import com.ampnet.web3provider.enums.RedisEntity
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class RedisRepositoryImpl(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val applicationProperties: ApplicationProperties
) : RedisRepository {

    private val hashOperations: HashOperations<String, String, Any> = redisTemplate.opsForHash()

    override fun getCache(key: String, hashKey: String?): Any? {
        return hashKey?.let { hashOperations.get(key, hashKey) }
    }

    override fun updateCache(redisEntity: RedisEntity, hashKey: String?, value: Any) {
        hashKey?.let {
            hashOperations.put(redisEntity.methodName, hashKey, value)
            redisTemplate.expire(redisEntity.methodName, Duration.ofSeconds(getTtlForRedisEntity(redisEntity)))
        }
    }

    override fun deleteCache(key: String, hashKey: String) {
        hashOperations.delete(key, hashKey)
    }

    private fun getTtlForRedisEntity(redisEntity: RedisEntity): Long {
        return when (redisEntity) {
            RedisEntity.BALANCE -> applicationProperties.redis.ethGetBalanceTtl
            RedisEntity.CODE -> applicationProperties.redis.ethGetCodeTtl
            RedisEntity.CHAIN_ID -> applicationProperties.redis.ethChainIdTtl
            RedisEntity.TRANSACTION_BY_HASH -> applicationProperties.redis.ethGetTransactionByHashTtl
        }
    }
}
