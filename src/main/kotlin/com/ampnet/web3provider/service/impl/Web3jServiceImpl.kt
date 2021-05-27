package com.ampnet.web3provider.service.impl

import com.ampnet.web3provider.config.ApplicationProperties
import com.ampnet.web3provider.exception.InvalidRequestException
import com.ampnet.web3provider.exception.ResourceNotFoundException
import com.ampnet.web3provider.service.Web3jService
import org.springframework.stereotype.Service
import org.web3j.exceptions.MessageDecodingException
import org.web3j.exceptions.MessageEncodingException
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.DefaultBlockParameterNumber
import org.web3j.protocol.exceptions.ClientConnectionException
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Numeric

@Service
class Web3jServiceImpl(applicationProperties: ApplicationProperties) : Web3jService {

    private val web3j = Web3j.build(HttpService(applicationProperties.provider.blockchainApi))

    @Throws(InvalidRequestException::class, ResourceNotFoundException::class)
    override fun getBalance(address: String, blockParameter: String): String {
        val defaultBlockParameter = getDefaultBlockParameter(blockParameter)
        return handleException {
            Numeric.encodeQuantity(web3j.ethGetBalance(address, defaultBlockParameter).send().balance)
        }
    }

    @Throws(InvalidRequestException::class, ResourceNotFoundException::class)
    override fun getCode(address: String, blockParameter: String): String {
        val defaultBlockParameter = getDefaultBlockParameter(blockParameter)
        return handleException { web3j.ethGetCode(address, defaultBlockParameter).send().code }
    }

    @Throws(InvalidRequestException::class, ResourceNotFoundException::class)
    override fun getStorageAt(address: String, position: String, blockParameter: String): String {
        val defaultBlockParameter = getDefaultBlockParameter(blockParameter)
        val positionInteger = try {
            Numeric.decodeQuantity(position)
        } catch (ex: MessageDecodingException) {
            throw InvalidRequestException("$position is not a valid parameter.", ex)
        }
        return handleException {
            web3j.ethGetStorageAt(address, positionInteger, defaultBlockParameter).send().data
        }
    }

    private fun getDefaultBlockParameter(blockParameter: String): DefaultBlockParameter {
        return try {
            DefaultBlockParameterName.fromString(blockParameter)
        } catch (ex: IllegalArgumentException) {
            try {
                DefaultBlockParameterNumber(Numeric.decodeQuantity(blockParameter))
            } catch (ex: MessageDecodingException) {
                throw InvalidRequestException("$blockParameter is not a valid block parameter.", ex)
            }
        }
    }

    private fun handleException(function: () -> String): String {
        try {
            return function.invoke()
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
