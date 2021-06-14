package com.ampnet.web3provider.service

import com.ampnet.web3provider.controller.pojo.JsonRpcRequest
import com.ampnet.web3provider.exception.JsonRpcException
import com.ampnet.web3provider.service.impl.DefaultProviderServiceImpl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.test.web.client.response.MockRestResponseCreators

class DefaultProviderServiceTest : JpaServiceTestBase() {

    private val defaultProviderService: DefaultProviderServiceImpl by lazy {
        DefaultProviderServiceImpl(restTemplate, applicationProperties, objectMapper, redisRepository)
    }

    private lateinit var testContext: TestContext

    @BeforeEach
    fun init() {
        testContext = TestContext()
    }

    @Test
    fun mustThrowExceptionOnErrorResponseFromProvider() {
        suppose("Provider returns bad response") {
            testContext.jsonRpcRequest = JsonRpcRequest(
                "2.0", balance.methodName, listOf(address, blockParameter), "1"
            )
            mockDefaultProviderResponse(
                objectMapper.writeValueAsString(testContext.jsonRpcRequest), MockRestResponseCreators.withServerError()
            )
        }

        verify("Default provider service throws jsonRpcException") {
            assertThrows<JsonRpcException> {
                defaultProviderService.getResponseAndUpdateCache(
                    testContext.jsonRpcRequest, balance, address + blockParameter
                )
            }
        }
        verify("Rest template called mocked server") { mockServer.verify() }
        verify("Balance is not stored in cache") {
            val balance = redisRepository.getCache(balance.methodName, address + blockParameter)
            assertThat(balance).isNull()
        }
    }

    private class TestContext {
        lateinit var jsonRpcRequest: JsonRpcRequest
    }
}
