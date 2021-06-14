package com.ampnet.web3provider.service.impl

import com.ampnet.web3provider.config.ApplicationProperties
import com.ampnet.web3provider.controller.pojo.JsonRpcRequest
import com.ampnet.web3provider.controller.pojo.ProviderResponse
import com.ampnet.web3provider.enums.RedisEntity
import com.ampnet.web3provider.repository.RedisRepository
import com.ampnet.web3provider.service.DefaultProviderService
import com.ampnet.web3provider.service.TransactionService
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class TransactionServiceImpl(
    private val redisRepository: RedisRepository,
    private val defaultProviderService: DefaultProviderService,
    private val applicationProperties: ApplicationProperties
) : TransactionService {

    companion object : KLogging()

    override fun getTransactionByHash(request: JsonRpcRequest): ProviderResponse {
        val txHash = request.params.getOrNull(0)
        logger.info { "Received request to get ${request.method} for txHash: $txHash" }
        val transactionByHash = RedisEntity.TransactionByHash(applicationProperties)
        redisRepository.getCache(transactionByHash.methodName, txHash.toString())
            ?.let { return ProviderResponse(request, it) }
        return defaultProviderService.getResponseAndUpdateCache(
            request, transactionByHash, txHash.toString()
        )
    }
}
