package kz.aura.merp.employee.bindingAdapter

import android.content.Intent
import android.view.View
import androidx.databinding.BindingAdapter
import kz.aura.merp.employee.ui.activity.ServiceApplicationActivity
import kz.aura.merp.employee.model.ServiceApplication

class MasterBinding {
    companion object {
        @BindingAdapter("android:sendServAppToServAppActivity")
        @JvmStatic
        fun sendServAppToServAppActivity(view: View, serviceApplication: ServiceApplication) {
            view.setOnClickListener {
                val intent = Intent(view.context, ServiceApplicationActivity::class.java)
                intent.putExtra("serviceApplication", serviceApplication)
                view.context.startActivity(intent)
            }
        }
    }
}