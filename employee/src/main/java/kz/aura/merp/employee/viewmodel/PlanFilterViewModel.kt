package kz.aura.merp.employee.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kz.aura.merp.employee.model.PlanFilter
import javax.inject.Inject

@HiltViewModel
class PlanFilterViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {
    val filterParams: MutableLiveData<PlanFilter> = MutableLiveData()

    fun apply(query: String, selectedSortFilter: Int, selectedStatusFilter: Int, selectedSearchBy: Int, problematic: Boolean) {
        filterParams.postValue(
            PlanFilter(
                query,
                selectedSortFilter,
                selectedStatusFilter,
                selectedSearchBy,
                problematic
            )
        )
    }

    fun clearFilter() {
        filterParams.postValue(getDefault())
    }

    fun getDefault() = PlanFilter("", 0,0, 0, false)
}