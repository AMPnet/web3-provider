package com.ampnet.web3provider.service

import com.ampnet.web3provider.TestBase
import com.ampnet.web3provider.enums.RedisEntity
import com.ampnet.web3provider.repository.ProviderRedisRepository
import com.ampnet.web3provider.service.impl.ProviderServiceImpl
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
import java.math.BigInteger

@ExtendWith(value = [SpringExtension::class, RestDocumentationExtension::class])
@SpringBootTest
class ProviderServiceTest : TestBase() {

    private val address = "0xAb5801a7D398351b8bE11C439e05C5B3259aeC9B"
    private val blockParameter = DefaultBlockParameterName.LATEST.toString().toLowerCase()

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, String>

    @Autowired
    private lateinit var providerRepository: ProviderRedisRepository

    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var web3jService: Web3jService

    private val providerService: ProviderServiceImpl by lazy {
        ProviderServiceImpl(web3jService, providerRepository)
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
                .thenReturn(BigInteger.TEN)
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
                post("/provider").content(json)
            ).andExpect(status().isOk).andReturn()
            val response: GetBalanceResponse = objectMapper.readValue(result.response.contentAsString)
            assertThat(response.result).isEqualTo(BigInteger.TEN.toString())
        }
    }

    @Test
    fun mustClearCacheAfterTtlExpires() {
        suppose("Web3j service will return balance") {
            Mockito.`when`(web3jService.getBalance(address, blockParameter))
                .thenReturn(BigInteger.TEN)
        }

        verify("Cache is cleared after 5 seconds") {
            providerService.getBalance(address, blockParameter)
            val cache = providerRepository.getBalance(address, blockParameter)
            val expiryTimeInSec = redisTemplate.getExpire(RedisEntity.BALANCE.key)
            Thread.sleep(RedisEntity.BALANCE.ttlInSec * 1000)
            val cacheAfter5sec = providerRepository.getBalance(address, blockParameter)
            assertThat(cache).isEqualTo(BigInteger.TEN)
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
