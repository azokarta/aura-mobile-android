package kz.aura.merp.employee.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kz.aura.merp.employee.util.LoadingType
import kz.aura.merp.employee.util.NetworkResult

class SharedViewModel(application: Application): AndroidViewModel(application) {

    val emptyData = MutableLiveData(true)
    val dataReceived = MutableLiveData(false)
    val response: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val loadingType: MutableLiveData<LoadingType> = MutableLiveData(LoadingType.PROGRESS_BAR)

    fun setResponse(response: NetworkResult<*>) = this.response.postValue(response)

    fun setLoadingType(loadingType: LoadingType) = this.loadingType.postValue(loadingType)

    fun showLoading() {
        emptyData.value = true
        dataReceived.value = false
    }

    fun hideLoading(dataIsEmpty: Boolean) {
        emptyData.value = dataIsEmpty
        dataReceived.value = true
    }
}