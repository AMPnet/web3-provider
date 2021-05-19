package com.ampnet.web3provider.service.impl

import com.ampnet.web3provider.service.ProviderService
import com.ampnet.web3provider.service.Web3jService
import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl
import mu.KLogging
import org.springframework.stereotype.Service
import java.math.BigInteger

@AutoJsonRpcServiceImpl
@Service
class ProviderServiceImpl(private val web3jService: Web3jService) : ProviderService {

    companion object : KLogging()

    override fun getBalance(address: String, blockParameter: Any): BigInteger {
        logger.info { "Received request to get eth_getBalance for address: $address and block: $blockParameter" }
        return web3jService.getBalance(address, blockParameter)
    }
}
