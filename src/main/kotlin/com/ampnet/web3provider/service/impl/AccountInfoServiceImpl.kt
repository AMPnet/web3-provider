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
        redisRepository.getCache(RedisEntity.BALANCE.key, address + blockParameter)?.let { return it }
        val balance = web3jService.getBalance(address, blockParameter)
        redisRepository.updateCache(RedisEntity.BALANCE, address + blockParameter, balance)
        return balance
    }

    override fun getCode(address: String, blockParameter: String): String {
        TODO("Not yet implemented")
    }

    override fun getStorageAt(address: String, position: String, blockParameter: String): String {
        TODO("Not yet implemented")
    }
}
