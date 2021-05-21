package kz.aura.merp.employee.bindingAdapter

import android.content.Intent
import android.view.View
import androidx.databinding.BindingAdapter
import kz.aura.merp.employee.ui.activity.PlanActivity
import kz.aura.merp.employee.model.Plan
import kz.aura.merp.employee.ui.activity.DailyPlanActivity

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
        @BindingAdapter("android:sendContractIdToDailyPlan")
        @JvmStatic
        fun sendContractIdToDailyPlan(view: View, contractId: Long) {
            view.setOnClickListener {
                val intent = Intent(view.context, DailyPlanActivity::class.java)
                intent.putExtra("contractId", contractId)
                view.context.startActivity(intent)
            }
        }
    }
}