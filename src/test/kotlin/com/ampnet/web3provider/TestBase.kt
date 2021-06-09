package com.ampnet.web3provider

import com.ampnet.web3provider.controller.pojo.JsonRpcRequest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
abstract class TestBase {

    protected fun suppose(@Suppress("UNUSED_PARAMETER") description: String, function: () -> Unit) {
        function.invoke()
    }

    protected fun verify(@Suppress("UNUSED_PARAMETER") description: String, function: () -> Unit) {
        function.invoke()
    }

    protected fun generateProviderResponse(request: JsonRpcRequest, result: Any): String {
        return """
            {
                "id": "${request.id}",
                "jsonrpc": "${request.jsonrpc}",
                "method": "${request.method}",
                "result": "$result"
            }
        """.trimIndent()
    }
}
