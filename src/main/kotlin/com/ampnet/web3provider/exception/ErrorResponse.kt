package com.ampnet.web3provider.exception

data class ErrorResponse(
    val jsonrpc: String? = "2.0",
    val error: JsonRpcError,
    val id: Any
)

data class JsonRpcError(
    val code: Int,
    val message: String
)
