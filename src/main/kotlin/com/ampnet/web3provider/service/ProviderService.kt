package com.ampnet.web3provider.service

import com.ampnet.web3provider.exception.InvalidRequestException
import com.ampnet.web3provider.exception.ResourceNotFoundException
import com.googlecode.jsonrpc4j.JsonRpcError
import com.googlecode.jsonrpc4j.JsonRpcErrors
import com.googlecode.jsonrpc4j.JsonRpcMethod
import com.googlecode.jsonrpc4j.JsonRpcService
import java.math.BigInteger

@JsonRpcService("/provider")
interface ProviderService {

    @JsonRpcMethod(value = "eth_getBalance")
    @JsonRpcErrors(
        value = [
            JsonRpcError(exception = InvalidRequestException::class, code = -32602),
            JsonRpcError(exception = ResourceNotFoundException::class, code = -32602)
        ]
    )
    fun getBalance(address: String, blockParameter: Any): BigInteger
}
