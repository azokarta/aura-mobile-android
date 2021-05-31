package kz.aura.merp.employee.bindingAdapter

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kz.aura.merp.employee.util.LoadingType
import kz.aura.merp.employee.util.NetworkResult

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


        // -----------------------------------------------------------
        @JvmStatic
        @BindingAdapter("android:response", "android:loadingType", requireAll = true)
        fun showLoading(view: View, res: NetworkResult<*>?, loadingType: LoadingType) {
            view.isVisible = res is NetworkResult.Loading<*> && loadingType == LoadingType.PROGRESS_BAR
        }

        @JvmStatic
        @BindingAdapter("android:showDataByResponse")
        fun showDataByResponse(view: View, res: NetworkResult<*>?) {
            view.isVisible = res is NetworkResult.Success
        }

        @JvmStatic
        @BindingAdapter("android:showError")
        fun showError(view: View, res: NetworkResult<*>?) {
            view.isVisible = res is NetworkResult.Error
        }

        @JvmStatic
        @BindingAdapter("android:noData")
        fun noData(view: View, res: NetworkResult<*>?) {
            view.isVisible = if (res is NetworkResult.Success) {
                when (res.data) {
                    is List<*> -> res.data.isNullOrEmpty()
                    else -> res.data == null
                }
            } else false
        }

        @JvmStatic
        @BindingAdapter("android:response", "android:loadingType", requireAll = true)
        fun refreshIndicator(view: SwipeRefreshLayout, res: NetworkResult<*>?, loadingType: LoadingType) {
            view.isRefreshing = res is NetworkResult.Loading && loadingType == LoadingType.SWIPE_REFRESH
        }

        @JvmStatic
        @BindingAdapter("android:enableByResponse")
        fun enableByResponse(view: View, res: NetworkResult<*>?) {
            println(res)
            view.isEnabled = res is NetworkResult.Success
        }

    }

}