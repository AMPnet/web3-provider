package com.ampnet.web3provider.service

import com.ampnet.web3provider.TestBase
import com.ampnet.web3provider.controller.pojo.JsonRpcRequest
import com.ampnet.web3provider.controller.pojo.ProviderResponse
import com.ampnet.web3provider.enums.RedisEntity
import com.ampnet.web3provider.repository.RedisRepository
import com.ampnet.web3provider.service.impl.AccountInformationServiceImpl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.context.WebApplicationContext
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.utils.Numeric
import java.math.BigInteger

@ExtendWith(value = [SpringExtension::class, RestDocumentationExtension::class])
@SpringBootTest
class AccountInformationServiceTest : TestBase() {

    private val address = "0xAb5801a7D398351b8bE11C439e05C5B3259aeC9B"
    private val blockParameter = DefaultBlockParameterName.LATEST.toString().toLowerCase()
    private val hexString = Numeric.encodeQuantity(BigInteger.TEN)

    private lateinit var testContext: TestContext

    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, String>

    @Autowired
    private lateinit var redisRepository: RedisRepository

    @MockBean
    private lateinit var defaultProviderService: DefaultProviderService

    private val accountInformationService: AccountInformationServiceImpl by lazy {
        AccountInformationServiceImpl(redisRepository, defaultProviderService)
    }

    @BeforeEach
    fun init(wac: WebApplicationContext, restDocumentation: RestDocumentationContextProvider) {
        testContext = TestContext()
    }

    @Test
    fun mustClearCacheForGetBalanceAfterTtlExpires() {
        suppose("Default provider service will return balance") {
            testContext.jsonRpcRequest = JsonRpcRequest(
                "2.0", RedisEntity.BALANCE.methodName, listOf(address, blockParameter), 1
            )
            Mockito.`when`(defaultProviderService.getResponse(testContext.jsonRpcRequest))
                .thenReturn(ProviderResponse(testContext.jsonRpcRequest, hexString))
        }

        verify("Cache is cleared after 5 seconds") {
            accountInformationService.getBalance(testContext.jsonRpcRequest)
            val cache = redisRepository.getCache(RedisEntity.BALANCE.methodName, address + blockParameter)
            val expiryTimeInSec = redisTemplate.getExpire(RedisEntity.BALANCE.methodName)
            Thread.sleep(RedisEntity.BALANCE.ttlInSec * 1000)
            val cacheAfter5sec = redisRepository.getCache(RedisEntity.BALANCE.methodName, address + blockParameter)
            assertThat(cache).isEqualTo(hexString)
            assertThat(cacheAfter5sec).isNull()
            assertThat(expiryTimeInSec).isEqualTo(RedisEntity.BALANCE.ttlInSec)
        }
    }

    private class TestContext {
        lateinit var jsonRpcRequest: JsonRpcRequest
    }
}
