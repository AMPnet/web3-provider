package com.ampnet.web3provider.controller.pojo

data class ProviderResponse(
    val jsonrpc: String?,
    val id: Any?,
    val result: Any?
) {
    constructor(request: JsonRpcRequest, result: Any?) : this(
        request.jsonrpc,
        request.id,
        result
    )
}
