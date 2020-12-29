package kz.aura.merp.customer.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class SharedViewModel(application: Application): AndroidViewModel(application) {

    val emptyData = MutableLiveData<Boolean>(true)
    val dataReceived = MutableLiveData<Boolean>(false)

    fun <T> checkData(data: ArrayList<T>) {
        emptyData.value = data.isEmpty()
        dataReceived.value = true
    }
}