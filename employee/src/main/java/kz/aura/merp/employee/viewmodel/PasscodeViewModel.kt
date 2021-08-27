package kz.aura.merp.employee.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kz.aura.merp.employee.base.AppPreferences
import javax.inject.Inject

@HiltViewModel
class PasscodeViewModel @Inject constructor(
    val preferences: AppPreferences,
    application: Application
): AndroidViewModel(application) {}