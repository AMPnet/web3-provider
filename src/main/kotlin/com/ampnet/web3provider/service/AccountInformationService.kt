package com.ampnet.web3provider.service

import com.ampnet.web3provider.controller.pojo.JsonRpcRequest
import com.ampnet.web3provider.controller.pojo.ProviderResponse

interface AccountInformationService {
    fun getBalance(request: JsonRpcRequest): ProviderResponse
    fun getCode(request: JsonRpcRequest): ProviderResponse
}
