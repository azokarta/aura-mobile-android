package kz.aura.merp.customer.presenters

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kz.aura.merp.customer.data.model.Auth
import kz.aura.merp.customer.data.model.Bonus
import kz.aura.merp.customer.data.model.ResponseHelper
import kz.aura.merp.customer.service.AuthApi
import kz.aura.merp.customer.service.ServiceBuilder
import kz.aura.merp.customer.util.Helpers
import kz.aura.merp.customer.views.IAuthorizationView
import kz.aura.merp.customer.views.IBonusView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

interface IAuthPresenter {
    fun fetchTracsactionId()
    fun fetchToken(transactionId: String)
}

class AuthPresenter(val iAuthorizationView: IAuthorizationView, val context: Context) : IAuthPresenter {
    private val apiService = ServiceBuilder.buildService(AuthApi::class.java, context)

//    fun fetchTransactionId() = viewModelScope.launch(Dispatchers.IO) {
//        println("DDDDDDDDDDDDDDDDDD")
//        try {
//            val response = apiService.fetchTransactionId()
//            println(response)
//            if (response.isSuccessful) {
//                transactionId.postValue(response.body()!!.data)
//            } else {
//                error.postValue(response.errorBody())
//            }
//        } catch (e: Exception) {
//            error.postValue(e)
//        }
//    }

//    fun fetchToken(transactionId: String) = viewModelScope.launch(Dispatchers.IO) {
//        try {
//            val phoneNumber = getPhoneNumber()
//            val response = apiService.fetchToken(phoneNumber, transactionId)
//
//            if (response.isSuccessful && response.body()!!.success) {
//                saveTokenAndStaff(getApplication<Application>().applicationContext, response.body()!!.data)
//                positionId.postValue(response.body()!!.data.userInfo.salaryDtoList[0].positionId)
//            } else {
//                onErrorToken.postValue(true)
//            }
//        } catch (e: Exception) {
//            error.postValue(e)
//        }
//    }

//    private fun getPhoneNumber(): String {
//        return PreferenceManager
//            .getDefaultSharedPreferences(getApplication())
//            .getString("phoneNumber", "")!!
//    }

    override fun fetchTracsactionId() {
        val response = apiService.fetchTransactionId()
        response.enqueue(object : Callback<ResponseHelper<String>> {
            override fun onFailure(call: Call<ResponseHelper<String>>, t: Throwable) {
                Helpers.exceptionHandler(t, context)
                iAuthorizationView.onError(t.message!!)
            }

            override fun onResponse(call: Call<ResponseHelper<String>>, response: Response<ResponseHelper<String>>) {
                if (response.isSuccessful && response.body()!!.success) {
                    iAuthorizationView.onSuccessTransactionId(response.body()!!.data)
                } else {
                    Helpers.exceptionHandler(response.errorBody()!!, context)
                    iAuthorizationView.onError(response.errorBody()!!)
                }
            }
        })
    }

    override fun fetchToken(transactionId: String) {
        val response = apiService.fetchToken(transactionId)
        response.enqueue(object : Callback<ResponseHelper<Auth>> {
            override fun onFailure(call: Call<ResponseHelper<Auth>>, t: Throwable) {
                Helpers.exceptionHandler(t, context)
                iAuthorizationView.onError(t.message!!)
            }

            override fun onResponse(call: Call<ResponseHelper<Auth>>, response: Response<ResponseHelper<Auth>>) {
                if (response.isSuccessful && response.body()!!.success) {
                    PreferenceManager.getDefaultSharedPreferences(context)
                            .edit()
                            .putString("token", response.body()!!.data.token)
                            .putLong("customer", response.body()!!.data.customerId)
                            .apply()
                    iAuthorizationView.onSuccessAuth()
                }
            }
        })
    }

}