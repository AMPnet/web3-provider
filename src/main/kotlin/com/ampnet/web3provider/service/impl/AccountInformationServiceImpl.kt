package com.ampnet.web3provider.service.impl

import com.ampnet.web3provider.controller.pojo.JsonRpcRequest
import com.ampnet.web3provider.controller.pojo.ProviderResponse
import com.ampnet.web3provider.enums.RedisEntity
import com.ampnet.web3provider.repository.RedisRepository
import com.ampnet.web3provider.service.AccountInformationService
import com.ampnet.web3provider.service.DefaultProviderService
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class AccountInformationServiceImpl(
    private val redisRepository: RedisRepository,
    private val defaultProviderService: DefaultProviderService
) : AccountInformationService {

    companion object : KLogging()

    override fun getBalance(request: JsonRpcRequest): ProviderResponse {
        val address = request.params[0]
        val blockParameter = request.params[1]
        logger().info { "Received request to get eth_getBalance for address: $address and block: $blockParameter" }
        val hashKey = address + blockParameter
        redisRepository.getCache(RedisEntity.BALANCE.methodName, hashKey)?.let { return ProviderResponse(request, it) }
        val providerResponse = defaultProviderService.getResponse(request)
        redisRepository.updateCache(RedisEntity.BALANCE, hashKey, providerResponse.result)
        return providerResponse
    }

    override fun getCode(request: JsonRpcRequest): ProviderResponse {
        val address = request.params[0]
        val blockParameter = request.params[1]
        logger.info { "Received request to get eth_getCode for address: $address and block: $blockParameter" }
        val hashKey = address + blockParameter
        redisRepository.getCache(RedisEntity.CODE.methodName, hashKey)?.let { return ProviderResponse(request, it) }
        val providerResponse = defaultProviderService.getResponse(request)
        redisRepository.updateCache(RedisEntity.CODE, hashKey, providerResponse.result)
        return providerResponse
    }

    override fun getStorageAt(request: JsonRpcRequest): ProviderResponse {
        val address = request.params[0]
        val position = request.params[1]
        val blockParameter = request.params[2]
        logger.info {
            "Received request to get eth_getStorageAt for address: " +
                "$address, position: $position and block: $blockParameter"
        }
        val hashKey = address + position + blockParameter
        redisRepository.getCache(RedisEntity.STORAGE_AT.methodName, hashKey)
            ?.let { return ProviderResponse(request, it) }
        val providerResponse = defaultProviderService.getResponse(request)
        redisRepository.updateCache(RedisEntity.STORAGE_AT, hashKey, providerResponse.result)
        return providerResponse
    }
}
