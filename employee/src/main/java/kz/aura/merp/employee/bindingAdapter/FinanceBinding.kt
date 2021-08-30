package kz.aura.merp.employee.bindingAdapter

import android.content.Intent
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import kz.aura.merp.employee.ui.finance.activity.PlanActivity
import kz.aura.merp.employee.ui.finance.activity.DailyPlanActivity

class FinanceBinding {
    companion object {

        @BindingAdapter("android:sendContractIdToPlanActivity")
        @JvmStatic
        fun sendContractIdToPlanActivity(view: View, contractId: Long) {
            view.setOnClickListener {
                val intent = Intent(view.context, PlanActivity::class.java)
                intent.putExtra("contractId", contractId)
                view.context.startActivity(intent)
            }
        }

        @BindingAdapter("android:sendDailyPlanIdToDailyPlan")
        @JvmStatic
        fun sendDailyPlanIdToDailyPlan(view: View, dailyPlanId: Long) {
            view.setOnClickListener {
                val intent = Intent(view.context, DailyPlanActivity::class.java)
                intent.putExtra("dailyPlanId", dailyPlanId)
                view.context.startActivity(intent)
            }
        }

        @BindingAdapter("android:avatar")
        @JvmStatic
        fun initProfileAvatar(imageView: ImageView, url: String) {
            val default = "https://cityhope.cc/wp-content/uploads/2020/01/default-avatar.png"
            if (url.isBlank()) {
//                Picasso.get()
//                    .load(default)
//                    .into(imageView)
            } else {

            }
        }

    }
}