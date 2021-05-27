package com.ampnet.web3provider.service

interface Web3jService {
    fun getBalance(address: String, blockParameter: String): String
    fun getCode(address: String, blockParameter: String): String
    fun getStorageAt(address: String, position: String, blockParameter: String): String
}
