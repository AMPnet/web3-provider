package com.ampnet.web3provider.service.impl

import com.ampnet.web3provider.enums.RedisEntity
import com.ampnet.web3provider.repository.RedisRepository
import com.ampnet.web3provider.service.AccountInfoService
import com.ampnet.web3provider.service.Web3jService
import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl
import mu.KLogging
import org.springframework.stereotype.Service

@AutoJsonRpcServiceImpl
@Service
class AccountInfoServiceImpl(
    private val web3jService: Web3jService,
    private val redisRepository: RedisRepository
) : AccountInfoService {

    companion object : KLogging()

    override fun getBalance(address: String, blockParameter: String): String {
        logger.info { "Received request to get eth_getBalance for address: $address and block: $blockParameter" }
        val hashKey = address + blockParameter
        redisRepository.getCache(RedisEntity.BALANCE.methodName, hashKey)?.let { return it }
        val balance = web3jService.getBalance(address, blockParameter)
        redisRepository.updateCache(RedisEntity.BALANCE, hashKey, balance)
        return balance
    }

    override fun getCode(address: String, blockParameter: String): String {
        logger.info { "Received request to get eth_getCode for address: $address and block: $blockParameter" }
        val hashKey = address + blockParameter
        redisRepository.getCache(RedisEntity.CODE.methodName, hashKey)?.let { return it }
        val code = web3jService.getCode(address, blockParameter)
        redisRepository.updateCache(RedisEntity.CODE, hashKey, code)
        return code
    }

    override fun getStorageAt(address: String, position: String, blockParameter: String): String {
        logger.info {
            "Received request to get eth_getBCode for address: $address, position: $position and block: $blockParameter"
        }
        val hashKey = address + position + blockParameter
        redisRepository.getCache(RedisEntity.STORAGE_AT.methodName, hashKey)
            ?.let { return it }
        val storage = web3jService.getStorageAt(address, position, blockParameter)
        redisRepository.updateCache(RedisEntity.STORAGE_AT, hashKey, storage)
        return storage
    }
}
