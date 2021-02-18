package kz.aura.merp.employee.bindingAdapter

import android.content.Intent
import android.view.View
import androidx.databinding.BindingAdapter
import kz.aura.merp.employee.activity.PlanActivity
import kz.aura.merp.employee.data.model.Client

class FinanceBinding {
    companion object {
        @BindingAdapter("android:sendClientToClientActivity")
        @JvmStatic
        fun sendClientToClientActivity(view: View, client: Client) {
            view.setOnClickListener {
                val intent = Intent(view.context, PlanActivity::class.java)
                intent.putExtra("client", client)
                view.context.startActivity(intent)
            }
        }
    }
}