package kz.aura.merp.employee.util

sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null,
    val status: Int? = null
) {

    class Success<T>(data: T): NetworkResult<T>(data)
    class Error<T>(message: String?, status: Int? = null, data: T? = null): NetworkResult<T>(data, message, status)
    class Loading<T>: NetworkResult<T>()

}
