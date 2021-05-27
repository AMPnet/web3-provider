package com.ampnet.web3provider.service

import com.ampnet.web3provider.TestBase
import com.ampnet.web3provider.enums.RedisEntity
import com.ampnet.web3provider.repository.RedisRepository
import com.ampnet.web3provider.service.impl.AccountInfoServiceImpl
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.utils.Numeric
import java.math.BigInteger

@ExtendWith(value = [SpringExtension::class, RestDocumentationExtension::class])
@SpringBootTest
class AccountInfoServiceTest : TestBase() {

    private val address = "0xAb5801a7D398351b8bE11C439e05C5B3259aeC9B"
    private val blockParameter = DefaultBlockParameterName.LATEST.toString().toLowerCase()
    private val balanceInHex = Numeric.encodeQuantity(BigInteger.TEN)

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, String>

    @Autowired
    private lateinit var redisRepository: RedisRepository

    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var web3jService: Web3jService

    private val providerService: AccountInfoServiceImpl by lazy {
        AccountInfoServiceImpl(web3jService, redisRepository)
    }

    @BeforeEach
    fun init(wac: WebApplicationContext, restDocumentation: RestDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
            .apply<DefaultMockMvcBuilder>(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
            .alwaysDo<DefaultMockMvcBuilder>(
                MockMvcRestDocumentation.document(
                    "{ClassName}/{methodName}",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint())
                )
            )
            .build()
    }

    @Test
    fun mustBeAbleToGetUserAccountBalance() {
        suppose("Web3j service will return balance") {
            Mockito.`when`(web3jService.getBalance(address, blockParameter))
                .thenReturn(balanceInHex)
        }

        verify("Provider service returns correct balance") {
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
                post("/account-info").content(json)
            ).andExpect(status().isOk).andReturn()
            val response: GetBalanceResponse = objectMapper.readValue(result.response.contentAsString)
            assertThat(response.result).isEqualTo(balanceInHex)
        }
    }

    @Test
    fun mustClearCacheAfterTtlExpires() {
        suppose("Web3j service will return balance") {
            Mockito.`when`(web3jService.getBalance(address, blockParameter))
                .thenReturn(balanceInHex)
        }

        verify("Cache is cleared after 5 seconds") {
            providerService.getBalance(address, blockParameter)
            val cache = redisRepository.getCache(RedisEntity.BALANCE.key, address + blockParameter)
            val expiryTimeInSec = redisTemplate.getExpire(RedisEntity.BALANCE.key)
            Thread.sleep(RedisEntity.BALANCE.ttlInSec * 1000)
            val cacheAfter5sec = redisRepository.getCache(RedisEntity.BALANCE.key, address + blockParameter)
            assertThat(cache).isEqualTo(balanceInHex)
            assertThat(cacheAfter5sec).isNull()
            assertThat(expiryTimeInSec).isEqualTo(RedisEntity.BALANCE.ttlInSec)
        }
    }

    data class GetBalanceResponse(
        val id: String,
        val jsonrpc: String,
        val result: String
    )
}
