package com.ampnet.web3provider.config

import com.ampnet.web3provider.enums.RedisEntity
import com.ampnet.web3provider.repository.RedisRepository
import org.springframework.stereotype.Service

@Service
class CacheCleanerService(val redisRepository: RedisRepository) {

    fun deleteBalance(hashKey: String) {
        redisRepository.deleteCache(RedisEntity.BALANCE.methodName, hashKey)
    }

    fun deleteCode(hashKey: String) {
        redisRepository.deleteCache(RedisEntity.CODE.methodName, hashKey)
    }

    fun deleteChainId() {
        redisRepository.deleteCache(RedisEntity.CHAIN_ID.methodName, RedisEntity.CHAIN_ID.methodName)
    }

    fun deleteTransactionByHash(hashKey: String) {
        redisRepository.deleteCache(RedisEntity.TRANSACTION_BY_HASH.methodName, hashKey)
    }
}
