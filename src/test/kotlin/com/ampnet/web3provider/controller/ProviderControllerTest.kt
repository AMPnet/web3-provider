package com.ampnet.web3provider.controller

import com.ampnet.web3provider.controller.pojo.JsonRpcRequest
import com.ampnet.web3provider.controller.pojo.ProviderResponse
import com.ampnet.web3provider.enums.RedisEntity
import com.ampnet.web3provider.service.pojo.Transaction
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.utils.Numeric
import java.math.BigInteger

class ProviderControllerTest : ControllerTestBase() {

    private val address = "0xAb5801a7D398351b8bE11C439e05C5B3259aeC9B"
    private val blockParameter = DefaultBlockParameterName.LATEST.value
    private val hexStringResult = Numeric.encodeQuantity(BigInteger.TEN)
    private val txHash = "0x88df016429689c079f3b2f6ad39fa052532c56795b733da78a91ebe6a713944b"

    private lateinit var testContext: TestContext

    @BeforeEach
    fun init() {
        testContext = TestContext()
        cacheCleanerService.deleteBalance(address + blockParameter)
        cacheCleanerService.deleteCode(address + blockParameter)
        cacheCleanerService.deleteChainId()
        cacheCleanerService.deleteTransactionByHash(txHash)
    }

    @Test
    fun mustBeAbleToGetUserAccountBalance() {
        suppose("Default provider service will return balance") {
            testContext.jsonRpcRequest = JsonRpcRequest(
                "2.0", RedisEntity.BALANCE.methodName, listOf(address, blockParameter), "1"
            )
            Mockito.`when`(defaultProviderService.getResponse(testContext.jsonRpcRequest))
                .thenReturn(ProviderResponse(testContext.jsonRpcRequest, hexStringResult))
        }

        verify("Provider controller returns correct result") {
            val json =
                """
                    {
                        "id":"1",
                        "jsonrpc":"2.0",
                        "method": "eth_getBalance",
                        "params":["$address", "$blockParameter"]
                    }
                """.trimIndent()
            val result = mockMvc.perform(
                post("/api").content(json).contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk).andReturn()
            val response: ProviderResponse = objectMapper.readValue(result.response.contentAsString)
            assertThat(response.result).isEqualTo(hexStringResult)
            assertThat(response.jsonrpc).isEqualTo(testContext.jsonRpcRequest.jsonrpc)
            assertThat(response.id).isEqualTo(testContext.jsonRpcRequest.id)
        }
        verify("Balance is saved inside the cache") {
            val balance = redisRepository.getCache(RedisEntity.BALANCE.methodName, address + blockParameter)
            assertThat(balance).isEqualTo(hexStringResult)
        }
    }

    @Test
    fun mustBeAbleToGetCode() {
        suppose("Default provider service will return code") {
            testContext.jsonRpcRequest = JsonRpcRequest(
                "2.0", RedisEntity.CODE.methodName, listOf(address, blockParameter), "1"
            )
            Mockito.`when`(defaultProviderService.getResponse(testContext.jsonRpcRequest))
                .thenReturn(ProviderResponse(testContext.jsonRpcRequest, hexStringResult))
        }

        verify("Provider service returns correct result") {
            val json =
                """
                    {
                        "id":"1",
                        "jsonrpc":"2.0",
                        "method": "eth_getCode",
                        "params":["$address", "$blockParameter"]
                    }
                """.trimIndent()
            val result = mockMvc.perform(
                post("/api").content(json).contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk).andReturn()
            val response: ProviderResponse = objectMapper.readValue(result.response.contentAsString)
            assertThat(response.result).isEqualTo(hexStringResult)
            assertThat(response.jsonrpc).isEqualTo(testContext.jsonRpcRequest.jsonrpc)
            assertThat(response.id).isEqualTo(testContext.jsonRpcRequest.id)
        }
        verify("Code is saved inside the cache") {
            val code = redisRepository.getCache(RedisEntity.CODE.methodName, address + blockParameter)
            assertThat(code).isEqualTo(hexStringResult)
        }
    }

    @Test
    fun mustBeAbleToGetChainId() {
        suppose("Default provider service will return chainId") {
            testContext.jsonRpcRequest = JsonRpcRequest(
                "2.0", RedisEntity.CHAIN_ID.methodName, listOf(), "1"
            )
            Mockito.`when`(defaultProviderService.getResponse(testContext.jsonRpcRequest))
                .thenReturn(ProviderResponse(testContext.jsonRpcRequest, hexStringResult))
        }

        verify("Provider service returns correct result") {
            val json =
                """
                    {
                        "id":"1",
                        "jsonrpc":"2.0",
                        "method": "eth_chainId",
                        "params":[]
                    }
                """.trimIndent()
            val result = mockMvc.perform(
                post("/api").content(json).contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk).andReturn()
            val response: ProviderResponse = objectMapper.readValue(result.response.contentAsString)
            assertThat(response.result).isEqualTo(hexStringResult)
            assertThat(response.jsonrpc).isEqualTo(testContext.jsonRpcRequest.jsonrpc)
            assertThat(response.id).isEqualTo(testContext.jsonRpcRequest.id)
        }
        verify("Chain id is saved inside the cache") {
            val chainId = redisRepository.getCache(RedisEntity.CHAIN_ID.methodName, RedisEntity.CHAIN_ID.methodName)
            assertThat(chainId).isEqualTo(hexStringResult)
        }
    }

    @Test
    fun mustBeAbleToGetTransactionByHash() {
        suppose("Default provider service will return transaction result") {
            testContext.jsonRpcRequest = JsonRpcRequest(
                "2.0", RedisEntity.TRANSACTION_BY_HASH.methodName, listOf(txHash), "1"
            )
            testContext.transaction = createTransaction()
            Mockito.`when`(defaultProviderService.getResponse(testContext.jsonRpcRequest))
                .thenReturn(ProviderResponse(testContext.jsonRpcRequest, testContext.transaction))
        }

        verify("Provider service returns correct result") {
            val json =
                """
                    {
                        "id":"1",
                        "jsonrpc":"2.0",
                        "method": "eth_getTransactionByHash",
                        "params":["$txHash"]
                    }
                """.trimIndent()
            val result = mockMvc.perform(
                post("/api").content(json).contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk).andReturn()
            val response: ProviderResponse = objectMapper.readValue(result.response.contentAsString)
            val transaction = Transaction.from(response.result as Map<String, String>)
            assertThat(transaction).isEqualTo(testContext.transaction)
            assertThat(response.jsonrpc).isEqualTo(testContext.jsonRpcRequest.jsonrpc)
            assertThat(response.id).isEqualTo(testContext.jsonRpcRequest.id)
        }
        verify("Transaction is saved inside the cache") {
            val transaction = redisRepository.getCache(RedisEntity.TRANSACTION_BY_HASH.methodName, txHash)
            assertThat(transaction).isEqualTo(testContext.transaction)
        }
    }

    private class TestContext {
        lateinit var jsonRpcRequest: JsonRpcRequest
        lateinit var transaction: Transaction
    }
}
