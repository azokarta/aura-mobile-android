package kz.aura.merp.employee.base

import kz.aura.merp.employee.util.ErrorStatus
import retrofit2.Response

sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null,
    val status: ErrorStatus? = null
) {

    class Success<T>(data: T? = null): NetworkResult<T>(data)
    class Error<T>(message: String?, status: ErrorStatus? = null): NetworkResult<T>(message = message, status = status)
    class Loading<T>: NetworkResult<T>()

}

inline fun <T> executeWithResponse(body: () -> Response<T>): NetworkResult<T> {
    val response = body.invoke()
    return try {
        if (response.isSuccessful) {
            NetworkResult.Success(response.body())
        } else {
            when (response.code()) {
                ErrorStatus.BAD_REQUEST.status -> NetworkResult.Error(e.message, ErrorStatus.BAD_REQUEST)
                ErrorStatus.INTERNAL_SERVER_ERROR.status -> NetworkResult.Error(e.message, ErrorStatus.INTERNAL_SERVER_ERROR)
                ErrorStatus.NOT_FOUND.status -> NetworkResult.Error(e.message, ErrorStatus.NOT_FOUND)
                ErrorStatus.UNAUTHORIZED.status -> NetworkResult.Error(e.message, ErrorStatus.UNAUTHORIZED)
                else -> NetworkResult.Error(e.message)
            }
        }
    } catch (e: Exception) {
        NetworkResult.Error(e.localizedMessage)
    }
}