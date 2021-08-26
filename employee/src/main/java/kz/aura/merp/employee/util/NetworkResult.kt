package kz.aura.merp.employee.util

sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null,
    val status: ErrorStatus? = null
) {

    class Success<T>(data: T? = null): NetworkResult<T>(data)
    class Error<T>(message: String?, status: ErrorStatus? = null): NetworkResult<T>(message = message, status = status)
    class Loading<T>: NetworkResult<T>()

}