package kz.aura.merp.employee.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kz.aura.merp.employee.util.LoadingType
import kz.aura.merp.employee.base.NetworkResult

class SharedViewModel : ViewModel() {

    val response: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val loadingType: MutableLiveData<LoadingType> = MutableLiveData(LoadingType.PROGRESS_BAR)

    fun setResponse(res: NetworkResult<*>) {
        response.postValue(res)
    }

    fun setLoadingType(loadingType: LoadingType) {
        this.loadingType.postValue(loadingType)
    }

}