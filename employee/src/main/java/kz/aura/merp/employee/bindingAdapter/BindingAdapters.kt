package kz.aura.merp.employee.bindingAdapter

import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kz.aura.merp.employee.util.LoadingType
import kz.aura.merp.employee.base.NetworkResult
import kz.aura.merp.employee.model.ResponseHelper

class BindingAdapters {

    companion object {

        @JvmStatic
        @BindingAdapter("android:response", "android:loadingType", requireAll = true)
        fun showLoading(view: ProgressBar, res: NetworkResult<*>?, loadingType: LoadingType) {
            view.isVisible = res is NetworkResult.Loading<*> && loadingType == LoadingType.PROGRESS_BAR
        }

        @JvmStatic
        @BindingAdapter("android:response", "android:loadingType", requireAll = true)
        fun showDataByResponse(view: View, res: NetworkResult<*>?, loadingType: LoadingType) {
            view.isVisible = when (res) {
                is NetworkResult.Success -> true
                is NetworkResult.Loading -> loadingType == LoadingType.SWIPE_REFRESH
                else -> false
            }
        }

        @JvmStatic
        @BindingAdapter("android:showError")
        fun showError(view: View, res: NetworkResult<*>?) {
            view.isVisible = res is NetworkResult.Error
        }

        @JvmStatic
        @BindingAdapter("android:noData")
        fun noData(view: View, res: NetworkResult<*>?) {
            if (res is NetworkResult.Success) {
                if (res.data is ResponseHelper<*>) {
                    view.isVisible = when (res.data.data) {
                        is List<*> -> res.data.data.isNullOrEmpty()
                        else -> res.data.data == null
                    }
                } else {
                    view.isVisible = when (res.data) {
                        is List<*> -> res.data.isNullOrEmpty()
                        else -> res.data == null
                    }
                }
            }
        }

        @JvmStatic
        @BindingAdapter("android:response", "android:loadingType")
        fun refreshIndicator(
            view: SwipeRefreshLayout,
            res: NetworkResult<*>?,
            loadingType: LoadingType
        ) {
            view.isRefreshing = res is NetworkResult.Loading && loadingType == LoadingType.SWIPE_REFRESH
        }

        @JvmStatic
        @BindingAdapter("android:enableByResponse")
        fun enableByResponse(view: View, res: NetworkResult<*>?) {
            view.isEnabled = res is NetworkResult.Success
        }

        @JvmStatic
        @BindingAdapter("android:isVisible")
        fun isVisible(view: View, visible: Boolean) {
            view.isVisible = visible
        }

    }

}