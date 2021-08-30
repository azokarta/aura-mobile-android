package kz.aura.merp.employee.viewmodel.finance

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kz.aura.merp.employee.base.AppPreferences
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val preferences: AppPreferences
) : ViewModel() {

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
}