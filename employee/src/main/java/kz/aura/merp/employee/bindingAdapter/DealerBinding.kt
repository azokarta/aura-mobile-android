package kz.aura.merp.employee.bindingAdapter

import android.content.Intent
import android.view.View
import androidx.databinding.BindingAdapter
import kz.aura.merp.employee.activity.DemoActivity
import kz.aura.merp.employee.data.model.Demo


class DealerBinding {

    companion object {
        @BindingAdapter("android:sendDemoToDemoActivity")
        @JvmStatic
        fun sendDemoToDemoActivity(view: View, demo: Demo) {
            view.setOnClickListener {
                val intent = Intent(view.context, DemoActivity::class.java)
                intent.putExtra("demo", demo)
                view.context.startActivity(intent)
            }
        }
    }
}