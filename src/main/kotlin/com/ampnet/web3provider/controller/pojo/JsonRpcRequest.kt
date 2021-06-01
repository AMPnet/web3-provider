package com.ampnet.web3provider.controller.pojo

data class JsonRpcRequest(
    val jsonrpc: String? = "2.0",
    val method: String,
    val params: List<String>,
    val id: Long = 0
)
