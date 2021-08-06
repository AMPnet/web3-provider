package com.ampnet.web3provider.exception

class InvalidRequestException(exceptionMessage: String, throwable: Throwable? = null) :
    Exception(exceptionMessage, throwable)

class JsonRpcException(val errorResponse: ErrorResponse) : Exception()
