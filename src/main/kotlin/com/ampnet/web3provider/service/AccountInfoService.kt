package com.ampnet.web3provider.service

import com.ampnet.web3provider.exception.InvalidRequestException
import com.ampnet.web3provider.exception.ResourceNotFoundException
import com.googlecode.jsonrpc4j.JsonRpcError
import com.googlecode.jsonrpc4j.JsonRpcErrors
import com.googlecode.jsonrpc4j.JsonRpcMethod
import com.googlecode.jsonrpc4j.JsonRpcService

@JsonRpcService("/account-info")
interface AccountInfoService {

    @JsonRpcMethod(value = "eth_getBalance")
    @JsonRpcErrors(
        value = [
            JsonRpcError(exception = InvalidRequestException::class, code = -32602),
            JsonRpcError(exception = ResourceNotFoundException::class, code = -32602)
        ]
    )
    fun getBalance(address: String, blockParameter: String): String

    @JsonRpcMethod(value = "eth_getCode")
    fun getCode(address: String, blockParameter: String): String

    @JsonRpcMethod(value = "eth_getStorageAt")
    fun getStorageAt(address: String, position: String, blockParameter: String): String
}
