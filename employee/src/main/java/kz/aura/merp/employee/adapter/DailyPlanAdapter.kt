package kz.aura.merp.employee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kz.aura.merp.employee.R
import kz.aura.merp.employee.databinding.DailyPlanRowBinding
import kz.aura.merp.employee.model.DailyPlan

class DailyPlanAdapter(private val dailyPlanListener: DailyPlanListener) : RecyclerView.Adapter<DailyPlanAdapter.DailyPlanViewHolder>() {

    var dataList = mutableListOf<DailyPlan>()

    interface DailyPlanListener {
        fun selectDailyPlan(id: Long)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyPlanViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DailyPlanRowBinding.inflate(layoutInflater, parent, false)
        return DailyPlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyPlanViewHolder, position: Int) {
        val plan: DailyPlan = dataList[position]
        holder.bind(plan)
    }

    fun setData(plans: List<DailyPlan>) {
        val clientDiffUtil = MobDiffUtil(dataList, plans)
        val clientDiffResult = DiffUtil.calculateDiff(clientDiffUtil)
        this.dataList.clear()
        this.dataList.addAll(plans)
        clientDiffResult.dispatchUpdatesTo(this)
    }

    fun clear() {
        dataList.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = dataList.size

    inner class DailyPlanViewHolder(private val binding: DailyPlanRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(plan: DailyPlan) {
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

            binding.root.setOnClickListener {
                plan.dailyPlanId?.let { it1 -> dailyPlanListener.selectDailyPlan(it1) }
            }

            displayPaymentMethodIcons(plan)
        }

        private fun displayPaymentMethodIcons(plan: DailyPlan) {
            when (plan.planResultId) {
                1L -> {
                    binding.resultImg.setImageResource(R.drawable.ic_baseline_help_outline_24)
                }
                2L -> {
                    binding.resultImg.setImageResource(R.drawable.ic_baseline_account_balance_wallet_24)
                }
                3L -> {
                    binding.resultImg.setImageResource(R.drawable.ic_baseline_date_range_24)
                }
                4L -> {
                    binding.resultImg.setImageResource(R.drawable.ic_baseline_close_24)
                }
                else -> {
                    binding.resultImg.visibility = View.GONE
                }
            }

        }
    }

}