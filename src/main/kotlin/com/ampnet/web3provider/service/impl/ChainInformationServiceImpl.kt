package com.ampnet.web3provider.service.impl

import com.ampnet.web3provider.controller.pojo.JsonRpcRequest
import com.ampnet.web3provider.controller.pojo.ProviderResponse
import com.ampnet.web3provider.enums.RedisEntity
import com.ampnet.web3provider.repository.RedisRepository
import com.ampnet.web3provider.service.ChainInformationService
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class ChainInformationServiceImpl(
    private val redisRepository: RedisRepository
) : ChainInformationService {

    companion object : KLogging()

    override fun getChainId(request: JsonRpcRequest): ProviderResponse {
        logger().info { "Received request to get ${request.method}" }
        return redisRepository.getResponseFromCacheOrProvider(RedisEntity.CHAIN_ID, request.method, request)
    }
}
