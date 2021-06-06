package com.ampnet.web3provider.service.impl

import com.ampnet.web3provider.controller.pojo.JsonRpcRequest
import com.ampnet.web3provider.controller.pojo.ProviderResponse
import com.ampnet.web3provider.enums.RedisEntity
import com.ampnet.web3provider.repository.RedisRepository
import com.ampnet.web3provider.service.AccountInformationService
import com.ampnet.web3provider.service.DefaultProviderService
import mu.KLogging
import org.springframework.stereotype.Service
import org.web3j.protocol.core.DefaultBlockParameterName

@Service
class AccountInformationServiceImpl(
    private val redisRepository: RedisRepository,
    private val defaultProviderService: DefaultProviderService
) : AccountInformationService {

    companion object : KLogging()

    override fun getBalance(request: JsonRpcRequest): ProviderResponse {
        if (request.params.isEmpty()) return defaultProviderService.getResponse(request)
        val address = request.params[0]
        val blockParameter = request.params.getOrNull(1) ?: DefaultBlockParameterName.LATEST.value
        logger().info { "Received request to get ${request.method} for address: $address and block: $blockParameter" }
        val hashKey = address.toString() + blockParameter
        return redisRepository.getResponseFromCacheOrProvider(RedisEntity.BALANCE, hashKey, request)
    }

    override fun getCode(request: JsonRpcRequest): ProviderResponse {
        if (request.params.isEmpty()) return defaultProviderService.getResponse(request)
        val address = request.params[0]
        val blockParameter = request.params.getOrNull(1) ?: DefaultBlockParameterName.LATEST.value
        logger.info { "Received request to get ${request.method} for address: $address and block: $blockParameter" }
        val hashKey = address.toString() + blockParameter
        return redisRepository.getResponseFromCacheOrProvider(RedisEntity.CODE, hashKey, request)
    }
}
