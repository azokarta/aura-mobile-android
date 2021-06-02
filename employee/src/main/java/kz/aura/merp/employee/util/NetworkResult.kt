package kz.aura.merp.employee.util

sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null,
    val status: ErrorStatus? = null
) {

    class Success<T>(data: T? = null): NetworkResult<T>(data)
    class Error<T>(message: String?, status: ErrorStatus? = null, data: T? = null): NetworkResult<T>(data, message, status)
    class Loading<T>: NetworkResult<T>()

}