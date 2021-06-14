package com.ampnet.web3provider.controller

import com.ampnet.web3provider.controller.pojo.JsonRpcRequest
import com.ampnet.web3provider.controller.pojo.ProviderResponse
import com.ampnet.web3provider.controller.pojo.Transaction
import com.ampnet.web3provider.enums.RedisEntity
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.client.RestTemplate
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.utils.Numeric
import java.math.BigInteger

class ProviderControllerTest : ControllerTestBase() {

    private val address = "0xAb5801a7D398351b8bE11C439e05C5B3259aeC9B"
    private val blockParameter = DefaultBlockParameterName.LATEST.value
    private val hexValue = Numeric.encodeQuantity(BigInteger.TEN)
    private val txHash = "0x88df016429689c079f3b2f6ad39fa052532c56795b733da78a91ebe6a713944b"

    private lateinit var testContext: TestContext
    private lateinit var mockServer: MockRestServiceServer

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @BeforeEach
    fun init() {
        testContext = TestContext()
        RedisEntity.values().forEach { redisTemplate.delete(it.methodName) }
    }

    @Test
    fun mustBeAbleToGetUserAccountBalance() {
        suppose("Default provider service will return balance") {
            testContext.jsonRpcRequest = JsonRpcRequest(
                "2.0", RedisEntity.BALANCE.methodName, listOf(address, blockParameter), "1"
            )
            testContext.jsonRpcRequestJson = objectMapper.writeValueAsString(testContext.jsonRpcRequest)
            mockDefaultProviderResponse(
                testContext.jsonRpcRequestJson, generateProviderResponse(testContext.jsonRpcRequest, hexValue)
            )
        }

        verify("Provider controller returns correct result") {

            val result = mockMvc.perform(
                post("/api").content(testContext.jsonRpcRequestJson).contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk).andReturn()
            val response: ProviderResponse = objectMapper.readValue(result.response.contentAsString)
            assertThat(response.result).isEqualTo(hexValue)
            assertThat(response.jsonrpc).isEqualTo(testContext.jsonRpcRequest.jsonrpc)
            assertThat(response.id).isEqualTo(testContext.jsonRpcRequest.id)
        }
        verify("Balance is saved inside the cache") {
            val balance = redisRepository.getCache(RedisEntity.BALANCE.methodName, address + blockParameter)
            assertThat(balance).isEqualTo(hexValue)
        }
        verify("Rest template called mocked server") { mockServer.verify() }
    }

    @Test
    fun mustBeAbleToGetCode() {
        suppose("Default provider service will return code") {
            testContext.jsonRpcRequest = JsonRpcRequest(
                "2.0", RedisEntity.CODE.methodName, listOf(address, blockParameter), "1"
            )
            testContext.jsonRpcRequestJson = objectMapper.writeValueAsString(testContext.jsonRpcRequest)
            mockDefaultProviderResponse(
                testContext.jsonRpcRequestJson, generateProviderResponse(testContext.jsonRpcRequest, hexValue)
            )
        }

        verify("Provider service returns correct result") {
            val result = mockMvc.perform(
                post("/api").content(testContext.jsonRpcRequestJson).contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk).andReturn()
            val response: ProviderResponse = objectMapper.readValue(result.response.contentAsString)
            assertThat(response.result).isEqualTo(hexValue)
            assertThat(response.jsonrpc).isEqualTo(testContext.jsonRpcRequest.jsonrpc)
            assertThat(response.id).isEqualTo(testContext.jsonRpcRequest.id)
        }
        verify("Code is saved inside the cache") {
            val code = redisRepository.getCache(RedisEntity.CODE.methodName, address + blockParameter)
            assertThat(code).isEqualTo(hexValue)
        }
        verify("Rest template called mocked server") { mockServer.verify() }
    }

    @Test
    fun mustBeAbleToGetChainId() {
        suppose("Default provider service will return chainId") {
            testContext.jsonRpcRequest = JsonRpcRequest(
                "2.0", RedisEntity.CHAIN_ID.methodName, listOf(), "1"
            )
            testContext.jsonRpcRequestJson = objectMapper.writeValueAsString(testContext.jsonRpcRequest)
            mockDefaultProviderResponse(
                testContext.jsonRpcRequestJson, generateProviderResponse(testContext.jsonRpcRequest, hexValue)
            )
        }

        verify("Provider service returns correct result") {
            val result = mockMvc.perform(
                post("/api").content(testContext.jsonRpcRequestJson).contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk).andReturn()
            val response: ProviderResponse = objectMapper.readValue(result.response.contentAsString)
            assertThat(response.result).isEqualTo(hexValue)
            assertThat(response.jsonrpc).isEqualTo(testContext.jsonRpcRequest.jsonrpc)
            assertThat(response.id).isEqualTo(testContext.jsonRpcRequest.id)
        }
        verify("Chain id is saved inside the cache") {
            val chainId = redisRepository.getCache(RedisEntity.CHAIN_ID.methodName, RedisEntity.CHAIN_ID.methodName)
            assertThat(chainId).isEqualTo(hexValue)
        }
        verify("Rest template called mocked server") { mockServer.verify() }
    }

    @Test
    fun mustBeAbleToGetTransactionByHash() {
        suppose("Default provider service will return transaction result") {
            testContext.transaction = createTransaction()
            testContext.jsonRpcRequest = JsonRpcRequest(
                "2.0", RedisEntity.TRANSACTION_BY_HASH.methodName, listOf(txHash), "1"
            )
            testContext.jsonRpcRequestJson = objectMapper.writeValueAsString(testContext.jsonRpcRequest)
            val response = """
            {
                "id": "${testContext.jsonRpcRequest.id}",
                "jsonrpc": "${testContext.jsonRpcRequest.jsonrpc}",
                "method": "${testContext.jsonRpcRequest.method}",
                "result": ${objectMapper.writeValueAsString(testContext.transaction)}
            }
            """.trimIndent()
            mockDefaultProviderResponse(
                testContext.jsonRpcRequestJson,
                response
            )
        }

        verify("Provider service returns correct result") {
            val result = mockMvc.perform(
                post("/api").content(testContext.jsonRpcRequestJson).contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk).andReturn()
            val response: ProviderResponse = objectMapper.readValue(result.response.contentAsString)
            val transaction = Transaction.from(response.result as Map<String, String>)
            assertThat(transaction).isEqualTo(testContext.transaction)
            assertThat(response.jsonrpc).isEqualTo(testContext.jsonRpcRequest.jsonrpc)
            assertThat(response.id).isEqualTo(testContext.jsonRpcRequest.id)
        }
        verify("Transaction is saved inside the cache") {
            val transaction = redisRepository.getCache(RedisEntity.TRANSACTION_BY_HASH.methodName, txHash)
            assertThat(Transaction.from(transaction as Map<String, String>)).isEqualTo(testContext.transaction)
            redisTemplate.delete(RedisEntity.TRANSACTION_BY_HASH.methodName)
        }
        verify("Rest template called mocked server") { mockServer.verify() }
    }

    private fun mockDefaultProviderResponse(
        request: String,
        response: String
    ) {
        mockServer = MockRestServiceServer.createServer(restTemplate)
        mockServer.expect(
            ExpectedCount.once(),
            requestTo(applicationProperties.provider.blockchainApi)
        )
            .andExpect(MockRestRequestMatchers.content().string(request))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(MockRestRequestMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andRespond(MockRestResponseCreators.withSuccess(response, MediaType.APPLICATION_JSON))
    }

    private class TestContext {
        lateinit var jsonRpcRequest: JsonRpcRequest
        lateinit var transaction: Transaction
        lateinit var jsonRpcRequestJson: String
    }
}
