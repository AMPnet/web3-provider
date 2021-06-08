package com.ampnet.web3provider.service.pojo

/*
Used for serialization/deserialization of object returned as a result from eth_getTransactionByHash
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS) needed in case retrieving it from RedisCache
*/
data class Transaction(
    val hash: String = "",
    val blockHash: String = "",
    val blockNumber: String = "",
    val from: String = "",
    val gas: String = "",
    val gasPrice: String = "",
    val input: String = "",
    val nonce: String = "",
    val r: String = "",
    val s: String = "",
    val to: String = "",
    val transactionIndex: String = "",
    val v: String = "",
    val value: String = ""
) {
    companion object {
        fun from(map: Map<String, String>) = object {
            val hash by map
            val blockHash by map
            val blockNumber by map
            val from by map
            val gas by map
            val gasPrice by map
            val input by map
            val nonce by map
            val r by map
            val s by map
            val to by map
            val transactionIndex by map
            val v by map
            val value by map

            val transaction = Transaction(
                hash, blockHash, blockNumber, from, gas, gasPrice,
                input, nonce, r, s, to, transactionIndex, v, value
            )
        }.transaction
    }
}
