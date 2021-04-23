package kz.aura.merp.employee.bindingAdapter

import android.content.Intent
import android.view.View
import androidx.databinding.BindingAdapter
import kz.aura.merp.employee.ui.activity.PlanActivity
import kz.aura.merp.employee.model.Plan

class FinanceBinding {
    companion object {
        @BindingAdapter("android:sendPlanToPlanActivity")
        @JvmStatic
        fun sendPlanToPlanActivity(view: View, plan: Plan) {
            view.setOnClickListener {
                val intent = Intent(view.context, PlanActivity::class.java)
                intent.putExtra("plan", plan)
                view.context.startActivity(intent)
            }
        }
    }
}