package kz.aura.merp.employee.bindingAdapter

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData

class BindingAdapters {

    companion object {

        @BindingAdapter("android:emptyDatabase", "android:dataReceived")
        @JvmStatic
        fun emptyDatabase(view: View, emptyDatabase: MutableLiveData<Boolean>, dataReceived: MutableLiveData<Boolean>) {
            if (dataReceived.value!! && emptyDatabase.value!!) {
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
        }

        @BindingAdapter("android:hideProgressBar")
        @JvmStatic
        fun hideProgressBar(view: View, visibility: Boolean) {
            when(visibility){
                true -> view.visibility = View.INVISIBLE
                false -> view.visibility = View.VISIBLE
            }
        }

        @BindingAdapter("android:visibleFilter")
        @JvmStatic
        fun visibleFilter(view: View, dataIsEmpty: Boolean) {
            when (dataIsEmpty) {
                true -> view.isVisible = false
                false -> view.isVisible = true
            }
        }

    }

}