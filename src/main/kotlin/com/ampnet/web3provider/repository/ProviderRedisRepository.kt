package com.ampnet.web3provider.repository

import java.math.BigInteger

interface ProviderRedisRepository {
    fun getBalance(address: String, blockParameter: String): BigInteger?
    fun updateBalance(address: String, blockParameter: String, balance: BigInteger)
    fun deleteBalance(address: String, blockParameter: String)
}
