package com.ampnet.web3provider.service.impl

import com.ampnet.web3provider.config.Provider
import com.ampnet.web3provider.exception.InvalidRequestException
import com.ampnet.web3provider.exception.ResourceNotFoundException
import com.ampnet.web3provider.service.Web3jService
import org.springframework.stereotype.Service
import org.web3j.exceptions.MessageEncodingException
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.DefaultBlockParameterNumber
import org.web3j.protocol.exceptions.ClientConnectionException
import org.web3j.protocol.http.HttpService
import java.math.BigInteger
import kotlin.jvm.Throws

@Service
class Web3jServiceImpl(provider: Provider) : Web3jService {

    private val web3j = Web3j.build(HttpService(provider.blockchainApi))

    @Throws(InvalidRequestException::class, ResourceNotFoundException::class)
    @Suppress("TooGenericExceptionCaught")
    override fun getBalance(address: String, blockParameter: Any): BigInteger {
        val defaultBlockParameter = if (blockParameter is String) {
            try {
                DefaultBlockParameterName.fromString(blockParameter.toString())
            } catch (ex: IllegalArgumentException) {
                throw InvalidRequestException("Default block parameter name: $blockParameter is not defined.", ex)
            }
        } else {
            blockParameter.toString().toBigIntegerOrNull()?.let {
                DefaultBlockParameterNumber(it)
            } ?: throw InvalidRequestException("Block parameter: $blockParameter is not a valid number.")
        }
        return try {
            web3j.ethGetBalance(address, defaultBlockParameter).send().balance
        } catch (ex: Exception) {
            when (ex) {
                is ClientConnectionException, is MessageEncodingException -> {
                    throw ResourceNotFoundException("Invalid method parameter(s).", ex)
                }
                else -> throw ResourceNotFoundException("Unexpected exception occurred.", ex)
            }
        }
    }
}
