package kz.aura.merp.employee.bindingAdapter

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import kz.aura.merp.employee.R
import kz.aura.merp.employee.data.model.Demo
import kz.aura.merp.employee.fragment.dealer.DealerFragmentDirections
import kz.aura.merp.employee.fragment.dealer.DemoFragmentDirections

class DealerBinding {

    companion object {
        @BindingAdapter("android:sendDemoToDemoFragment")
        @JvmStatic
        fun sendDemoToDemoFragment(view: View, demo: Demo) {
            view.setOnClickListener {
                val action = DealerFragmentDirections.actionDealerFragmentToDemoFragment(demo)
                it.findNavController().navigate(action)
            }
        }
    }
}