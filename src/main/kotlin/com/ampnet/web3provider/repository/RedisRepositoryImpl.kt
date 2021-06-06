package com.ampnet.web3provider.repository

import com.ampnet.web3provider.controller.pojo.JsonRpcRequest
import com.ampnet.web3provider.controller.pojo.ProviderResponse
import com.ampnet.web3provider.enums.RedisEntity
import com.ampnet.web3provider.service.DefaultProviderService
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class RedisRepositoryImpl(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val defaultProviderService: DefaultProviderService
) : RedisRepository {

    private val hashOperations: HashOperations<String, String, Any> = redisTemplate.opsForHash()

    override fun getCache(key: String, hashKey: String): Any? = hashOperations.get(key, hashKey)

    override fun updateCache(redisEntity: RedisEntity, hashKey: String, value: Any) {
        hashOperations.put(redisEntity.methodName, hashKey, value)
        redisTemplate.expire(redisEntity.methodName, Duration.ofSeconds(redisEntity.ttlInSec))
    }

    override fun deleteCache(key: String, hashKey: String) {
        hashOperations.delete(key, hashKey)
    }

    override fun getResponseFromCacheOrProvider(
        entity: RedisEntity,
        hashKey: String,
        request: JsonRpcRequest
    ): ProviderResponse {
        getCache(entity.methodName, hashKey)?.let { return ProviderResponse(request, it) }
        val providerResponse = defaultProviderService.getResponse(request)
        providerResponse.result?.let { updateCache(entity, hashKey, it) }
        return providerResponse
    }
}
