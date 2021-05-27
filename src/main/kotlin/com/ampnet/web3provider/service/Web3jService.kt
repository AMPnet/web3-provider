package com.ampnet.web3provider.service

interface Web3jService {
    fun getBalance(address: String, blockParameter: String): String
}
