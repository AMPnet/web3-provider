package com.ampnet.web3provider.service

import com.ampnet.web3provider.controller.pojo.JsonRpcRequest
import com.ampnet.web3provider.controller.pojo.ProviderResponse

interface DefaultProviderService {
    fun getResponse(request: JsonRpcRequest): ProviderResponse
}
