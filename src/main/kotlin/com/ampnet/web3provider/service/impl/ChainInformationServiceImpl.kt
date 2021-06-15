package com.ampnet.web3provider.service.impl

import com.ampnet.web3provider.controller.pojo.JsonRpcRequest
import com.ampnet.web3provider.controller.pojo.ProviderResponse
import com.ampnet.web3provider.enums.RedisEntity
import com.ampnet.web3provider.repository.RedisRepository
import com.ampnet.web3provider.service.ChainInformationService
import com.ampnet.web3provider.service.DefaultProviderService
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class ChainInformationServiceImpl(
    private val redisRepository: RedisRepository,
    private val defaultProviderService: DefaultProviderService
) : ChainInformationService {

    companion object : KLogging()

    override fun getChainId(request: JsonRpcRequest): ProviderResponse {
        logger.info { "Received request to get ${request.method}" }
        redisRepository.getCache(RedisEntity.CHAIN_ID.methodName, request.method)?.let {
            return ProviderResponse(request, it)
        }
        return defaultProviderService.getResponseAndUpdateCache(request, RedisEntity.CHAIN_ID, request.method)
    }
}
