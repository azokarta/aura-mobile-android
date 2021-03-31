package kz.aura.merp.employee.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class SharedViewModel(application: Application): AndroidViewModel(application) {

    val emptyData = MutableLiveData(true)
    val dataReceived = MutableLiveData(false)

    fun <T> checkData(data: ArrayList<T>) {
        emptyData.value = data.isEmpty()
        dataReceived.value = true
    }

    fun showLoading() {
        emptyData.value = true
        dataReceived.value = false
    }

    fun hideLoading() {
        emptyData.value = false
        dataReceived.value = true
    }
}