package com.ampnet.web3provider.service

import com.ampnet.web3provider.TestBase
import com.ampnet.web3provider.config.ApplicationProperties
import com.ampnet.web3provider.repository.RedisRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.ResponseCreator
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.web.client.RestTemplate
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.utils.Numeric
import java.math.BigInteger

@ExtendWith(SpringExtension::class)
@SpringBootTest
abstract class JpaServiceTestBase : TestBase() {

    protected val address = "0xAb5801a7D398351b8bE11C439e05C5B3259aeC9B"
    protected val blockParameter = DefaultBlockParameterName.LATEST.toString().toLowerCase()
    protected val hexValue: String = Numeric.encodeQuantity(BigInteger.TEN)

    @Autowired
    protected lateinit var redisTemplate: RedisTemplate<String, String>

    @Autowired
    protected lateinit var redisRepository: RedisRepository

    @Autowired
    protected lateinit var applicationProperties: ApplicationProperties

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Autowired
    protected lateinit var restTemplate: RestTemplate

    protected lateinit var mockServer: MockRestServiceServer

    protected fun mockDefaultProviderResponse(
        request: String,
        response: ResponseCreator
    ) {
        mockServer = MockRestServiceServer.createServer(restTemplate)
        mockServer.expect(
            ExpectedCount.once(),
            MockRestRequestMatchers.requestTo(applicationProperties.provider.blockchainApi)
        )
            .andExpect(MockRestRequestMatchers.content().string(request))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(MockRestRequestMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andRespond(response)
    }
}
