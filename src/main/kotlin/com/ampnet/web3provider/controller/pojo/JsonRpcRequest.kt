package com.ampnet.web3provider.controller.pojo

data class JsonRpcRequest(
    val jsonrpc: String,
    val method: String,
    val params: List<Any>,
    val id: Any?
)
