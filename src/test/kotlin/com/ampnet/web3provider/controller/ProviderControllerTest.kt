package com.ampnet.web3provider.controller

import com.ampnet.web3provider.TestBase
import com.ampnet.web3provider.controller.pojo.JsonRpcRequest
import com.ampnet.web3provider.controller.pojo.ProviderResponse
import com.ampnet.web3provider.enums.RedisEntity
import com.ampnet.web3provider.service.DefaultProviderService
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
import org.springframework.http.MediaType
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
class ProviderControllerTest : TestBase() {

    private val address = "0xAb5801a7D398351b8bE11C439e05C5B3259aeC9B"
    private val blockParameter = DefaultBlockParameterName.LATEST.value
    private val balance = Numeric.encodeQuantity(BigInteger.TEN)
    private val code = Numeric.encodeQuantity(BigInteger.ONE)

    private lateinit var testContext: TestContext

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var defaultProviderService: DefaultProviderService

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
        testContext = TestContext()
    }

    @Test
    fun mustBeAbleToGetUserAccountBalance() {
        suppose("Default provider service will return balance") {
            testContext.jsonRpcRequest = JsonRpcRequest(
                "2.0", RedisEntity.BALANCE.methodName, listOf(address, blockParameter), "1"
            )
            Mockito.`when`(defaultProviderService.getResponse(testContext.jsonRpcRequest))
                .thenReturn(ProviderResponse(testContext.jsonRpcRequest, balance))
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
            assertThat(response.result).isEqualTo(balance)
            assertThat(response.jsonrpc).isEqualTo(testContext.jsonRpcRequest.jsonrpc)
            assertThat(response.id).isEqualTo(testContext.jsonRpcRequest.id)
        }
    }

    @Test
    fun mustBeAbleToGetCode() {
        suppose("Default provider service will return code") {
            testContext.jsonRpcRequest = JsonRpcRequest(
                "2.0", RedisEntity.CODE.methodName, listOf(address, blockParameter), "1"
            )
            Mockito.`when`(defaultProviderService.getResponse(testContext.jsonRpcRequest))
                .thenReturn(ProviderResponse(testContext.jsonRpcRequest, code))
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
            assertThat(response.result).isEqualTo(code)
            assertThat(response.jsonrpc).isEqualTo(testContext.jsonRpcRequest.jsonrpc)
            assertThat(response.id).isEqualTo(testContext.jsonRpcRequest.id)
        }
    }

    private class TestContext {
        lateinit var jsonRpcRequest: JsonRpcRequest
    }
}
