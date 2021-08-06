package com.ampnet.web3provider.service.impl

import com.ampnet.web3provider.config.ApplicationProperties
import com.ampnet.web3provider.controller.pojo.JsonRpcRequest
import com.ampnet.web3provider.controller.pojo.ProviderResponse
import com.ampnet.web3provider.enums.RedisEntity
import com.ampnet.web3provider.exception.ErrorCode
import com.ampnet.web3provider.exception.ErrorResponse
import com.ampnet.web3provider.exception.JsonRpcException
import com.ampnet.web3provider.repository.RedisRepository
import com.ampnet.web3provider.service.DefaultProviderService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import mu.KLogging
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity

@Service
class DefaultProviderServiceImpl(
    private val restTemplate: RestTemplate,
    private val applicationProperties: ApplicationProperties,
    private val objectMapper: ObjectMapper,
    private val redisRepository: RedisRepository
) : DefaultProviderService {

    companion object : KLogging()

    @Throws(JsonRpcException::class)
    override fun getResponse(request: JsonRpcRequest): ProviderResponse {
        logger.debug { "Received request: $request" }
        try {
            val responseEntity =
                restTemplate.postForEntity<ProviderResponse>(applicationProperties.provider.blockchainApi, request)
            return responseEntity.body ?: throw JsonRpcException(ErrorResponse(request, ErrorCode.INTERNAL_ERROR))
        } catch (ex: HttpClientErrorException) {
            val errorResponse = objectMapper.readValue<ErrorResponse>(ex.responseBodyAsString)
            throw JsonRpcException(errorResponse)
        } catch (ex: RestClientException) {
            throw JsonRpcException(ErrorResponse(request, ErrorCode.INTERNAL_ERROR))
        }
    }

    @Throws(JsonRpcException::class)
    override fun getResponseAndUpdateCache(
        request: JsonRpcRequest,
        redisEntity: RedisEntity,
        hashKey: String?
    ): ProviderResponse {
        val response = getResponse(request)
        val result = response.result
        logger.info {
            "Received response $response from web3 provider and request to update cache with " +
                "result: $result for entity: ${redisEntity.methodName} and hashKey: $hashKey." +
                if (result != null && hashKey != null) "Updating cache with $result." else "Not updating cache."
        }
        result?.let { redisRepository.updateCache(redisEntity, hashKey, it) }
        return response
    }
}
