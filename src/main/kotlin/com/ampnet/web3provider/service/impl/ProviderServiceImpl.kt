package com.ampnet.web3provider.service.impl

import com.ampnet.web3provider.repository.ProviderRedisRepository
import com.ampnet.web3provider.service.ProviderService
import com.ampnet.web3provider.service.Web3jService
import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl
import mu.KLogging
import org.springframework.stereotype.Service
import java.math.BigInteger

@AutoJsonRpcServiceImpl
@Service
class ProviderServiceImpl(
    private val web3jService: Web3jService,
    private val providerRedisRepository: ProviderRedisRepository
) : ProviderService {

    companion object : KLogging()

    override fun getBalance(address: String, blockParameter: String): BigInteger {
        logger.info { "Received request to get eth_getBalance for address: $address and block: $blockParameter" }
        providerRedisRepository.getBalance(address, blockParameter)?.let { return it }
        val balance = web3jService.getBalance(address, blockParameter)
        providerRedisRepository.updateBalance(address, blockParameter, balance)
        return balance
    }
}
