package com.ampnet.web3provider.controller

import com.ampnet.web3provider.controller.pojo.JsonRpcRequest
import com.ampnet.web3provider.controller.pojo.ProviderResponse
import com.ampnet.web3provider.enums.RedisEntity
import com.ampnet.web3provider.service.AccountInformationService
import com.ampnet.web3provider.service.DefaultProviderService
import mu.KLogging
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ProviderController(
    private val accountInformationService: AccountInformationService,
    private val defaultProviderService: DefaultProviderService
) {

    companion object : KLogging()

    @PostMapping("/provider", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun provider(@RequestBody request: JsonRpcRequest): ResponseEntity<ProviderResponse> {
        logger().debug { "Received request for: ${request.method}" }
        val response = when (request.method) {
            RedisEntity.BALANCE.methodName -> accountInformationService.getBalance(request)
            RedisEntity.CODE.methodName -> accountInformationService.getCode(request)
            RedisEntity.STORAGE_AT.methodName -> accountInformationService.getStorageAt(request)
            else -> defaultProviderService.getResponse(request)
        }
        return ResponseEntity.ok(response)
    }
}
