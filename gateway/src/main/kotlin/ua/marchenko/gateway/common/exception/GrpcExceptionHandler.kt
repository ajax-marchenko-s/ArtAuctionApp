package ua.marchenko.gateway.common.exception

import com.google.rpc.Code
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import net.devh.boot.grpc.server.advice.GrpcAdvice
import com.google.rpc.Status
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler
import ua.marchenko.core.auction.exception.InvalidAuctionOperationException

import ua.marchenko.core.common.exception.NotFoundException

@GrpcAdvice
class GrpcExceptionHandler {

    @GrpcExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: NotFoundException): StatusRuntimeException {
        val status = Status.newBuilder().also {
            it.code = Code.NOT_FOUND.number
            it.message = ex.message
        }.build()

        return StatusProto.toStatusRuntimeException(status)
    }

    @GrpcExceptionHandler(InvalidAuctionOperationException::class)
    fun handleInvalidAuctionOperationException(ex: InvalidAuctionOperationException): StatusRuntimeException {
        val status = Status.newBuilder().also {
            it.code = Code.INVALID_ARGUMENT_VALUE
            it.message = ex.message
        }.build()
        return StatusProto.toStatusRuntimeException(status)
    }

    @GrpcExceptionHandler(Exception::class)
    fun handleException(ex: Exception): StatusRuntimeException {
        val status = Status.newBuilder().also {
            it.code = Code.INTERNAL.number
            it.message = ex.message
        }.build()
        return StatusProto.toStatusRuntimeException(status)
    }
}
