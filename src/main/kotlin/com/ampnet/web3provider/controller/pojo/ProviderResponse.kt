package com.ampnet.web3provider.controller.pojo

data class ProviderResponse(
    val jsonrpc: String? = "2.0",
    val id: Any = 0,
    val result: String
) {
    constructor(request: JsonRpcRequest, result: String) : this(
        request.jsonrpc,
        request.id,
        result
    )
}
