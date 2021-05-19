package com.ampnet.web3provider.service

import java.math.BigInteger

interface Web3jService {
    fun getBalance(address: String, blockParameter: Any): BigInteger
}
