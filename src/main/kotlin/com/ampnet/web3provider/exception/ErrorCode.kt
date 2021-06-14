package com.ampnet.web3provider.exception

enum class ErrorCode(val code: Int, val message: String, var meaning: String) {

    INVALID_PARAMS(-32602, "Invalid params", "Invalid method parameter(s)."),
    INTERNAL_ERROR(-32603, "Internal error", "Internal JSON-RPC error.")
}
