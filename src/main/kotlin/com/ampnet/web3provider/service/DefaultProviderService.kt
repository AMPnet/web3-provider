package com.ampnet.web3provider.service

import com.ampnet.web3provider.controller.pojo.JsonRpcRequest
import com.ampnet.web3provider.controller.pojo.ProviderResponse
import com.ampnet.web3provider.enums.RedisEntity

interface DefaultProviderService {
    fun getResponse(request: JsonRpcRequest): ProviderResponse
    fun getResponseAndUpdateCache(request: JsonRpcRequest, redisEntity: RedisEntity, hashKey: String?): ProviderResponse
}
