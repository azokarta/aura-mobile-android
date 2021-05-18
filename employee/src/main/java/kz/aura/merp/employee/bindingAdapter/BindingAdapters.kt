package kz.aura.merp.employee.bindingAdapter

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class BindingAdapters {

    companion object {

        @BindingAdapter("android:emptyDatabase", "android:dataReceived")
        @JvmStatic
        fun emptyDatabase(view: View, emptyDatabase: Boolean, dataReceived: Boolean) {
            view.isVisible = dataReceived && emptyDatabase
        }

        @BindingAdapter("android:hideProgressBar")
        @JvmStatic
        fun hideProgressBar(view: View, visibility: Boolean) {
            when(visibility){
                true -> view.visibility = View.INVISIBLE
                false -> view.visibility = View.VISIBLE
            }
        }

        @BindingAdapter("android:showRecyclerView")
        @JvmStatic
        fun showRecyclerView(view: View, visibility: Boolean) {
            when(visibility){
                true -> view.isVisible = true
                false -> view.isVisible = false
            }
        }

        @BindingAdapter("android:swipeRefreshByData")
        @JvmStatic
        fun showRefreshIndicatorByData(view: SwipeRefreshLayout, dataReceived: Boolean) {
            when(dataReceived){
                true -> view.isRefreshing = false
                false -> view.isRefreshing = true
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