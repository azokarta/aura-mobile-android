package kz.aura.merp.employee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.R
import kz.aura.merp.employee.model.Plan
import kz.aura.merp.employee.databinding.PlanRowBinding

class PlansAdapter(private val onClickListener: OnClickListener? = null) : ListAdapter<Plan, PlansAdapter.PlansViewHolder>(PlansDiffUtil())  {

    interface OnClickListener {
        fun sendToDailyPlan(contractId: Long) {}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlansViewHolder {
        return PlansViewHolder.from(parent, onClickListener)
    }

    override fun onBindViewHolder(holder: PlansViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PlansViewHolder(private val binding: PlanRowBinding, private val onClickListener: OnClickListener? = null) : RecyclerView.ViewHolder(binding.root) {
        fun bind(plan: Plan) {
            val textColor: Int = when {
                plan.paymentOverDueDays!! > 0 -> ContextCompat.getColor(binding.root.context, R.color.red)
                plan.paymentOverDueDays < 0 -> ContextCompat.getColor(binding.root.context, R.color.green)
                plan.paymentOverDueDays == 0 -> ContextCompat.getColor(binding.root.context, R.color.yellow)
                else -> R.color.black
            }

            binding.plan = plan
            binding.paymentOverdueDays.text = if (plan.paymentOverDueDays < 0) {
                plan.paymentOverDueDays.toString().drop(1)
            } else {
                plan.paymentOverDueDays.toString()
            }
            binding.paymentOverdueDays.setTextColor(textColor)
            binding.executePendingBindings()
            displayPaymentMethodIcons(plan)

            binding.toDailyPlan.setOnClickListener {
                onClickListener?.sendToDailyPlan(plan.contractId)
            }
        }

        private fun displayPaymentMethodIcons(plan: Plan) {
            val paymentMethodId = plan.planPaymentMethodId
            val bankDrawable = when (plan.planPaymentBankId) {
                2L -> R.drawable.ic_forte_bank
                4L -> R.drawable.ic_halyk_bank
                5L -> R.drawable.ic_center_credit_bank
                6L -> R.drawable.ic_atf_bank
                7L -> R.drawable.ic_kaspi_bank
                else -> R.drawable.ic_baseline_account_balance_24
            }

            binding.paymentMethodImg.visibility = View.VISIBLE
            binding.bankImg.visibility = View.VISIBLE

            when (plan.planResultId) {
                1L -> {
                    binding.paymentMethodImg.setImageResource(R.drawable.ic_baseline_help_outline_24)
                    binding.bankImg.visibility = View.GONE
                }
                2L -> {
                    when (paymentMethodId) {
                        1L -> {
                            binding.paymentMethodImg.setImageResource(R.drawable.ic_baseline_money_24)
                            binding.bankImg.visibility = View.GONE
                        }
                        2L -> {
                            binding.paymentMethodImg.setImageResource(R.drawable.ic_outline_change_circle_24)
                            binding.bankImg.setImageResource(bankDrawable)
                        }
                        3L -> {
                            binding.paymentMethodImg.setImageResource(R.drawable.ic_baseline_account_balance_24)
                            binding.bankImg.setImageResource(bankDrawable)
                        }
                    }
                }
                3L -> {
                    binding.paymentMethodImg.setImageResource(R.drawable.ic_baseline_date_range_24)
                    binding.bankImg.visibility = View.GONE
                }
                4L -> {
                    binding.paymentMethodImg.setImageResource(R.drawable.ic_baseline_close_24)
                    binding.bankImg.visibility = View.GONE
                }
                else -> {
                    binding.paymentMethodImg.visibility = View.GONE
                    binding.bankImg.visibility = View.GONE
                }
            }

        }

        companion object {
            fun from(parent: ViewGroup, onClickListener: OnClickListener?): PlansViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PlanRowBinding.inflate(layoutInflater, parent, false)
                return PlansViewHolder(binding, onClickListener)
            }
        }
    }

    private class PlansDiffUtil : DiffUtil.ItemCallback<Plan>() {
        override fun areItemsTheSame(oldItem: Plan, newItem: Plan): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Plan, newItem: Plan): Boolean = oldItem.contractId == newItem.contractId
    }

}