package com.ampnet.web3provider.exception

import com.ampnet.web3provider.controller.pojo.JsonRpcRequest

data class ErrorResponse(
    val jsonrpc: String,
    val error: JsonRpcError,
    val id: Any?,
) {
    constructor(request: JsonRpcRequest, errorCode: ErrorCode) : this (
        request.jsonrpc,
        JsonRpcError(errorCode),
        request.id
    )
}

data class JsonRpcError(
    val code: Int,
    val message: String,
    val data: Any?
) {
    constructor(errorCode: ErrorCode) : this (
        errorCode.code,
        errorCode.message,
        null
    )
}
