package com.ampnet.web3provider.repository

import java.math.BigInteger

interface ProviderRedisRepository {
    fun getBalance(address: String, blockParameter: Any): BigInteger?
    fun updateBalance(address: String, blockParameter: Any, balance: BigInteger)
    fun deleteBalance(address: String, blockParameter: Any)
}
