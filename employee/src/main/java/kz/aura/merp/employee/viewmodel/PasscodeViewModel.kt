package kz.aura.merp.employee.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kz.aura.merp.employee.data.DataStoreRepository
import javax.inject.Inject

@HiltViewModel
class PasscodeViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    application: Application
): AndroidViewModel(application) {

    val passcode: MutableLiveData<String> = MutableLiveData()

    fun getPasscode() = viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepository.passCodeFlow.collect { value ->
            passcode.postValue(value)
        }
    }

    fun savePasscode(passcode: String) = viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepository.savePassCode(passcode)
    }

}