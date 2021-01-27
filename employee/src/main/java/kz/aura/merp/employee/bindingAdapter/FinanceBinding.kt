package kz.aura.merp.employee.bindingAdapter

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import kz.aura.merp.employee.data.model.Client
import kz.aura.merp.employee.fragment.finance.FinanceFragmentDirections

class FinanceBinding {
    companion object {
        @BindingAdapter("android:sendClientToClientFragment")
        @JvmStatic
        fun sendClientToClientFragment(view: View, client: Client) {
            view.setOnClickListener {
                val action = FinanceFragmentDirections.actionFinanceFragmentToClientFragment(client)
                it.findNavController().navigate(action)
            }
        }
    }
}