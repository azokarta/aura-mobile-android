package kz.aura.merp.employee.base

import android.content.Context
import com.google.gson.Gson
import kz.aura.merp.employee.R
import kz.aura.merp.employee.model.Error
import kz.aura.merp.employee.util.ErrorStatus
import kz.aura.merp.employee.util.isNetworkConnected
import retrofit2.Response

sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null,
    val status: ErrorStatus? = null,
    val trace: String? = null
) {

    class Success<T>(data: T? = null): NetworkResult<T>(data)
    class Error<T>(message: String?, status: ErrorStatus? = null, trace: String? = null): NetworkResult<T>(message = message, status = status, trace = trace)
    class Loading<T>: NetworkResult<T>()

}

inline fun <T> executeWithResponse(context: Context, body: () -> Response<T>): NetworkResult<T> {
    if (!isNetworkConnected(context)) {
        return NetworkResult.Error(context.getString(R.string.internet_is_not_connected), ErrorStatus.NETWORK_IS_NOT_CONNECTED)
    }

    return try {
        val response = body.invoke()

        if (response.isSuccessful) {
            NetworkResult.Success(response.body())
        } else {
            val error = Gson().fromJson(response.errorBody()?.charStream(), Error::class.java)
            when (response.code()) {
                ErrorStatus.BAD_REQUEST.status -> NetworkResult.Error(error.message, ErrorStatus.BAD_REQUEST, error.trace)
                ErrorStatus.INTERNAL_SERVER_ERROR.status -> NetworkResult.Error(error.message, ErrorStatus.INTERNAL_SERVER_ERROR, error.trace)
                ErrorStatus.NOT_FOUND.status -> NetworkResult.Error(error.message, ErrorStatus.NOT_FOUND, error.trace)
                ErrorStatus.UNAUTHORIZED.status -> NetworkResult.Error(error.message, ErrorStatus.UNAUTHORIZED, error.trace)
                else -> NetworkResult.Error(error.message, trace = error.trace)
            }
        }
    } catch (e: Exception) {
        NetworkResult.Error(e.localizedMessage)
    }
}