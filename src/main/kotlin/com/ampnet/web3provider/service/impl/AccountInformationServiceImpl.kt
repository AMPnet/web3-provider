package com.ampnet.web3provider.service.impl

import com.ampnet.web3provider.config.ApplicationProperties
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
    private val defaultProviderService: DefaultProviderService,
    private val applicationProperties: ApplicationProperties
) : AccountInformationService {

    companion object : KLogging()

    override fun getBalance(request: JsonRpcRequest): ProviderResponse {
        val address = request.params.getOrNull(0)
        val blockParameter = request.params.getOrNull(1) ?: DefaultBlockParameterName.LATEST.value
        logger.info { "Received request to get ${request.method} for address: $address and block: $blockParameter" }
        val hashKey = address?.let { it.toString() + blockParameter }
        val balance = RedisEntity.Balance(applicationProperties)
        redisRepository.getCache(balance.methodName, hashKey)?.let { return ProviderResponse(request, it) }
        return defaultProviderService.getResponseAndUpdateCache(request, balance, hashKey)
    }

    override fun getCode(request: JsonRpcRequest): ProviderResponse {
        val address = request.params.getOrNull(0)
        val blockParameter = request.params.getOrNull(1) ?: DefaultBlockParameterName.LATEST.value
        logger.info { "Received request to get ${request.method} for address: $address and block: $blockParameter" }
        val hashKey = address?.let { it.toString() + blockParameter }
        val code = RedisEntity.Code(applicationProperties)
        redisRepository.getCache(code.methodName, hashKey)?.let { return ProviderResponse(request, it) }
        return defaultProviderService.getResponseAndUpdateCache(request, code, hashKey)
    }
}
