package com.ampnet.web3provider.service

import com.ampnet.web3provider.TestBase
import com.ampnet.web3provider.config.Provider
import com.ampnet.web3provider.exception.ResourceNotFoundException
import com.ampnet.web3provider.service.impl.Web3jServiceImpl
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(Provider::class)
class Web3jServiceTest : TestBase() {

    private val address = "0xAb5801a7D398351b8bE11C439e05C5B3259aeC9B"

    @Autowired
    private lateinit var provider: Provider

    private val web3jService: Web3jServiceImpl by lazy {
        Web3jServiceImpl(provider)
    }

    @Test
    fun mustThrowExceptionForInvalidMethodParameters() {
        verify("Must throw exception for invalid block number") {
            assertThrows<ResourceNotFoundException> { web3jService.getBalance(address, -5) }
        }
    }
}
