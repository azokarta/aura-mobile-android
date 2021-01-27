package kz.aura.merp.employee.bindingAdapter

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import kz.aura.merp.employee.data.model.ServiceApplication
import kz.aura.merp.employee.fragment.master.MasterFragmentDirections

class MasterBinding {
    companion object {
        @BindingAdapter("android:sendServiceApplicationToServiceApplicationFragment")
        @JvmStatic
        fun sendServiceApplicationToServiceApplicationFragment(view: View, serviceApplication: ServiceApplication) {
            view.setOnClickListener {
                val action = MasterFragmentDirections.actionMasterFragmentToServiceApplicationFragment(serviceApplication)
                it.findNavController().navigate(action)
            }
        }
    }
}