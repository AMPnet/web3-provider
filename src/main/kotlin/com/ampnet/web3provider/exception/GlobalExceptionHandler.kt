package com.ampnet.web3provider.exception

import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    companion object : KLogging()

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JsonRpcException::class)
    fun handleJsonRpcException(exception: JsonRpcException): ErrorResponse {
        logger.info("JsonRpcException", exception)
        return exception.errorResponse
    }
}
