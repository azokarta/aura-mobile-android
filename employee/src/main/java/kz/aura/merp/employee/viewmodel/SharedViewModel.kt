package kz.aura.merp.employee.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class SharedViewModel(application: Application): AndroidViewModel(application) {

    val emptyData = MutableLiveData(true)
    val dataReceived = MutableLiveData(false)

    fun showLoading() {
        emptyData.value = true
        dataReceived.value = false
    }

    fun hideLoading(dataIsEmpty: Boolean) {
        emptyData.value = dataIsEmpty
        dataReceived.value = true
    }
}