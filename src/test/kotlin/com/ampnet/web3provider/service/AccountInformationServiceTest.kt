package com.ampnet.web3provider.service

import com.ampnet.web3provider.controller.pojo.JsonRpcRequest
import com.ampnet.web3provider.enums.RedisEntity
import com.ampnet.web3provider.service.impl.AccountInformationServiceImpl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.client.response.MockRestResponseCreators

class AccountInformationServiceTest : JpaServiceTestBase() {

    private lateinit var testContext: TestContext

    @Autowired
    protected lateinit var defaultProviderService: DefaultProviderService

    private val accountInformationService: AccountInformationServiceImpl by lazy {
        AccountInformationServiceImpl(redisRepository, defaultProviderService)
    }

    @BeforeEach
    fun init() {
        testContext = TestContext()
    }

    @Test
    fun mustClearCacheForGetBalanceAfterTtlExpires() {
        suppose("Default provider service will return balance") {
            testContext.jsonRpcRequest = JsonRpcRequest(
                "2.0", RedisEntity.BALANCE.methodName, listOf(address, blockParameter), 1
            )
            mockDefaultProviderResponse(
                objectMapper.writeValueAsString(testContext.jsonRpcRequest),
                MockRestResponseCreators.withSuccess(
                    generateProviderResponse(testContext.jsonRpcRequest, hexValue),
                    MediaType.APPLICATION_JSON
                )
            )
        }

        verify("Cache is cleared after 5 seconds") {
            accountInformationService.getBalance(testContext.jsonRpcRequest)
            val cache = redisRepository.getCache(RedisEntity.BALANCE.methodName, address + blockParameter)
            val expiryTimeInSec = redisTemplate.getExpire(RedisEntity.BALANCE.methodName)
            Thread.sleep(RedisEntity.BALANCE.ttlInSec * 1000)
            val cacheAfter5sec = redisRepository.getCache(RedisEntity.BALANCE.methodName, address + blockParameter)
            assertThat(cache).isEqualTo(hexValue)
            assertThat(cacheAfter5sec).isNull()
            assertThat(expiryTimeInSec).isEqualTo(RedisEntity.BALANCE.ttlInSec)
        }
    }

    private class TestContext {
        lateinit var jsonRpcRequest: JsonRpcRequest
    }
}
