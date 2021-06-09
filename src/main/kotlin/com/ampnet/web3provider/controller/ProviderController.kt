package com.ampnet.web3provider.controller

import com.ampnet.web3provider.controller.pojo.JsonRpcRequest
import com.ampnet.web3provider.controller.pojo.ProviderResponse
import com.ampnet.web3provider.enums.RedisEntity
import com.ampnet.web3provider.service.AccountInformationService
import com.ampnet.web3provider.service.ChainInformationService
import com.ampnet.web3provider.service.DefaultProviderService
import com.ampnet.web3provider.service.TransactionService
import mu.KLogging
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ProviderController(
    private val accountInformationService: AccountInformationService,
    private val chainInformationService: ChainInformationService,
    private val transactionService: TransactionService,
    private val defaultProviderService: DefaultProviderService
) {

    companion object : KLogging()

    @PostMapping("/api", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun provider(@RequestBody request: JsonRpcRequest): ResponseEntity<ProviderResponse> {
        logger.debug { "Received request for: ${request.method}" }
        val response = when (request.method) {
            RedisEntity.BALANCE.methodName -> accountInformationService.getBalance(request)
            RedisEntity.CODE.methodName -> accountInformationService.getCode(request)
            RedisEntity.CHAIN_ID.methodName -> chainInformationService.getChainId(request)
            RedisEntity.TRANSACTION_BY_HASH.methodName -> transactionService.getTransactionByHash(request)
            else -> defaultProviderService.getResponse(request)
        }
        return ResponseEntity.ok(response)
    }
}
