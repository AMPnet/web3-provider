package com.ampnet.web3provider.repository

import com.ampnet.web3provider.enums.RedisEntity
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.math.BigInteger
import java.time.Duration

@Repository
class ProviderRedisRepositoryImpl(private val redisTemplate: RedisTemplate<String, String>) : ProviderRedisRepository {

    private val hashOperations: HashOperations<String, String, BigInteger> = redisTemplate.opsForHash()

    override fun getBalance(address: String, blockParameter: String): BigInteger? {
        return hashOperations.get(RedisEntity.BALANCE.key, getKey(address, blockParameter))
    }

    override fun updateBalance(address: String, blockParameter: String, balance: BigInteger) {
        hashOperations.put(RedisEntity.BALANCE.key, getKey(address, blockParameter), balance)
        redisTemplate.expire(RedisEntity.BALANCE.key, Duration.ofSeconds(RedisEntity.BALANCE.ttlInSec))
    }

    override fun deleteBalance(address: String, blockParameter: String) {
        hashOperations.delete(RedisEntity.BALANCE.key, getKey(address, blockParameter))
    }

    private fun getKey(address: String, blockParameter: String) = address + blockParameter
}
